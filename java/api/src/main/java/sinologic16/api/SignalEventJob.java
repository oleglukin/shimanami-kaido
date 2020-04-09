package sinologic16.api;

import java.io.Serializable;

import javax.annotation.PostConstruct;

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
import org.springframework.stereotype.Component;


@Component
public class SignalEventJob implements Serializable {

    private static final long serialVersionUID = -1389385671736977959L;

    @PostConstruct
    public void start() {

        String folder = "/home/oleg/code/github/oleglukin/shimanami-kaido/java/api/input/";

        SparkSession spark = SparkSession.builder().master("local").appName("SignalEventJob").getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");
        System.out.println("SignalEventJob. Reading from folder '" + folder + "'");

        Dataset<String> ds = spark.readStream().format("json").option("inferSchema", "true").text(folder).as(Encoders.STRING());
        
        StructType schema = new StructType()
            .add("id_sample", DataTypes.StringType, false)
            .add("num_id", DataTypes.StringType, true)
            .add("id_location", DataTypes.StringType, true)
            .add("id_signal_par", DataTypes.StringType, true)
            .add("id_detected", DataTypes.StringType, true)
            .add("id_class_det", DataTypes.StringType, true);

        Dataset<Row> parsed = ds.withColumn("jsonData", functions.from_json(functions.col("value"),schema)).select("jsonData.*");

        parsed.printSchema();

        Dataset<Row> grouped = parsed.groupBy("id_location", "id_detected").count();
        
        grouped.printSchema();

        
        grouped.writeStream().foreach(
            new ForeachWriter<Row>() {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean open(long partitionId, long version) {
                    return true; // Open connection
                }

                @Override public void process(Row e) {
                    // Write string to connection
                    String idLocation = e.getAs("id_location");
                    String idDetected = e.getAs("id_detected");
                    long count = e.getAs("count");
                    System.out.println("id_location: " + idLocation
                        + "\tidDetected: " + idDetected
                        + "\tcount: " + count);
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