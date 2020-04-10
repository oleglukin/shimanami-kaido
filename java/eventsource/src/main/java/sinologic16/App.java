package sinologic16;

import java.io.*;
import java.util.Random;


public class App 
{
    public static void main( String[] args )
    {
        int events = 16;
        int maxLocations = 3;
        String fileName = "input/events.json";

        String[] locations = getLocations(maxLocations);

        System.out.println("Generating " + events + " events for " + locations.length + " locations");
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), "utf-8"));
            
            SignalEvent e;
            

            for (int i = 0; i < events; i++) {

                e = new SignalEvent();
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

                String json = getJson(e);
                if (i < (events - 1)) json = json + "\n";
                writer.write(json);

            }
        } catch (IOException ex) {
            System.out.println(ex); // Report
        } finally {
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
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
}
