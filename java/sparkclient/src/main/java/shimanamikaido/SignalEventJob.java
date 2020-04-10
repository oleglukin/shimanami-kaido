package shimanamikaido;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.ForeachWriter;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import scala.Option;

public class SignalEventJob {
    public static void main(String[] args) {
        // default config values
        String appName = "SignalEventJob";
        String sparkMaster = "local";
        String inputFolder = "/tmp/input";
        //String apiEndpoint = "http://localhost:8080/api/updateaggregation";

        try (InputStream input = SignalEventJob.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Unable to find config.properties");
                return;
            }

            prop.load(input);

            String appNameProperty = prop.getProperty("appName");
            if (appNameProperty != null)
                appName = appNameProperty;

            String sparkMasterProperty = prop.getProperty("sparkMaster");
            if (sparkMasterProperty != null)
                sparkMaster = sparkMasterProperty;

            String inputFolderProperty = prop.getProperty("inputFolder");
            if (inputFolderProperty != null)
                inputFolder = inputFolderProperty;

            // String apiEndpointProperty = prop.getProperty("apiEndpoint");
            // if (apiEndpointProperty != null)
            //     apiEndpoint = apiEndpointProperty;

        } catch (IOException ex) {
            System.out.printf("Error readin properties: {}", ex.getMessage());
        }

        SparkSession spark = SparkSession.builder().master(sparkMaster).appName(appName).getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");

        Option<String> webUIUrl = spark.sparkContext().uiWebUrl();

        System.out.println("Initiated Spark context." 
                + "\nWebUI: " + webUIUrl.get() 
                + "\nApp name: " + appName
                + "\nReading data from folder '" + inputFolder + "'");

        Dataset<String> ds = spark.readStream().format("json").option("inferSchema", "true").text(inputFolder).as(Encoders.STRING());

        StructType schema = new StructType()
                .add("id_sample", DataTypes.StringType, false)
                .add("num_id", DataTypes.StringType, true)
                .add("id_location", DataTypes.StringType, true)
                .add("id_signal_par", DataTypes.StringType, true)
                .add("id_detected", DataTypes.StringType, true)
                .add("id_class_det", DataTypes.StringType, true);

        Dataset<Row> parsed = ds.withColumn("jsonData", functions.from_json(functions.col("value"), schema)).select("jsonData.*");

        System.out.println("Parsed data schema:");
        parsed.printSchema();

        Dataset<Row> grouped = parsed.groupBy("id_location", "id_detected").count();

        System.out.println("Grouped data schema:");
        grouped.printSchema();

        grouped.writeStream().foreach(new ForeachWriter<Row>() {
            private static final long serialVersionUID = 1L;
            //private final String endpoint = "http://localhost:8080/api/updateaggregation";
            private HttpClient httpClient;

            @Override
            public boolean open(long partitionId, long version) {
                httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
                return true;
            }

            @Override
            public void process(Row e) {
                // Write string to connection
                String idLocation = e.getAs("id_location");
                String idDetected = e.getAs("id_detected");
                long count = e.getAs("count");

                String endpoint = "http://localhost:8080/api/updateaggregation?id_location="+idLocation+"&id_detected="+idDetected+"&count="+count;

                HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(""))
                    .uri(URI.create(endpoint))
                    .setHeader("User-Agent", "Spark Job")
                    .build();

                try {
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (IOException | InterruptedException e1) {
                    e1.getMessage();
                }
            }

            @Override public void close(Throwable errorOrNull) {} // Close the connection
        }
        ).outputMode(OutputMode.Update())
        .start();


        try {
            spark.streams().awaitAnyTermination();
        } catch (StreamingQueryException e) {
            System.out.println(e.getMessage());
        }


    }
}
