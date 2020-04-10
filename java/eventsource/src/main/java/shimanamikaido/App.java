package shimanamikaido;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.util.Random;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class App 
{
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();


    public static void main( String[] args )
    {
        int events = 16;
        int maxLocations = 3;
        int maxIntervalMs = 0;
        String apiEndpoint = "http://localhost:8080/api/";

        try (InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Unable to find config.properties");
                return;
            }

            prop.load(input);

            events = tryParseInt(prop.getProperty("events"), events);
            maxLocations = tryParseInt(prop.getProperty("maxLocations"), maxLocations);
            maxIntervalMs = tryParseInt(prop.getProperty("maxIntervalMs"), maxIntervalMs);

            String apiEndpointProperty = prop.getProperty("appName");
            if (apiEndpointProperty != null) apiEndpoint = apiEndpointProperty;

        } catch (IOException ex) {
            System.out.printf("Error reading properties: {}", ex.getMessage());
        }


        String[] locations = getLocations(maxLocations);

        System.out.println("Generating " + events + " events for " + locations.length + " locations. Max interval between requests: " + maxIntervalMs + " ms.");

        try {
            for (int i = 0; i < events; i++) {

                String json = generateRandomEvent(locations, i);

                int delay = getRandomInt(0, maxIntervalMs);
                if (delay > 0) TimeUnit.MILLISECONDS.sleep(delay);


                HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(URI.create(apiEndpoint))
                    .setHeader("User-Agent", "Events Source")
                    .header("Content-Type", "application/json")
                    .build();

                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("\nFinished. Locations:");
        for (int i = 0; i < locations.length; i++) {
            System.out.println(locations[i]);
        }
    }


    private static String generateRandomEvent(String[] locations, int i) {
        SignalEvent e = new SignalEvent();

        e.id_sample = "f" + getRandomInt(i + 1, i * 3 + 19) + getRandomString(3);
        e.num_id = getRandomString(3) + "#" + getRandomString(2) + getRandomInt(120, 995);

        e.id_location = locations[getRandomInt(0, locations.length-1)];
        
        e.id_signal_par = "0x" + getRandomString(2) + getRandomInt(17, 99) + getRandomString(2);
        if (getRandomInt(1, 99) % 2 == 0) {
            e.id_detected = "None";
        } else {
            e.id_detected = "Nan";
        }
        e.id_class_det = "req" + getRandomInt(16, 94);
        return getJson(e);
    }


    private static String[] getLocations(int maxLocations) {
        String[] result = new String[maxLocations];
        for (int i = 0; i < maxLocations; i++) {
            if (i % 3 == 0) {
                result[i] = getRandomString(7);
            } else {
                result[i] = getRandomInt(2661, 7941) + "." + getRandomInt(120, 9940);
            }
        }
        return result;
    }

    private static int getRandomInt(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
    }

    
    private static String getRandomString(int targetStringLength) {
		int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
    
        String generatedString = random.ints(leftLimit, rightLimit + 1)
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    
        return generatedString;
    }

    
    private static String getJson(SignalEvent e) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id_sample\":\"" + e.id_sample + "\",");
        sb.append("\"num_id\":\"" + e.num_id + "\",");
        sb.append("\"id_location\":\"" + e.id_location + "\",");
        sb.append("\"id_signal_par\":\"" + e.id_signal_par + "\",");
        sb.append("\"id_detected\":\"" + e.id_detected + "\",");
        sb.append("\"id_class_det\":\"" + e.id_class_det + "\"");
        sb.append("}");
        return sb.toString();
    }


    private static int tryParseInt(String value, int defaultVal) {
        try {
            if (value == null) value = "";
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}