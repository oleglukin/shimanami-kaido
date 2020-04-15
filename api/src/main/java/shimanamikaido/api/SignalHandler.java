package shimanamikaido.api;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import shimanamikaido.api.model.SignalEvent;

@Component
public class SignalHandler {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private final List<SignalEvent> accumulatedEvents = Collections.synchronizedList(new ArrayList<SignalEvent>());

    @Value("${exchangeFolder}")
    private String exchangeFolder;


    @PostConstruct
    private void postConstruct() {
        if (!exchangeFolder.endsWith("/")) exchangeFolder = exchangeFolder + "/";
    }


    public void addEvent(SignalEvent e) {
        synchronized (accumulatedEvents) {
            accumulatedEvents.add(e);
        }
    }


    /**
     * Run this method over scheduled intervals. If there are any new events - dump them to a text file
     */
    @Scheduled(fixedRate = 5000)
	public void dumpEventsToFile() {
        synchronized (accumulatedEvents) {
            if(accumulatedEvents.size() > 0) {
                String filepath = exchangeFolder + dateFormat.format(new Date()) + ".txt";

                try {
                    FileWriter writer = new FileWriter(filepath); 

                    accumulatedEvents.forEach(e -> {
                        try {
                            writer.write(e.toString() + System.lineSeparator());
                        } catch (IOException ioex) {
                            throw new RuntimeException(ioex);
                        }
                    });
                    
                    writer.close();
                } catch (Exception ex) {
                    System.out.println("Failed to write to file '" + filepath + "'.\n" + ex.getMessage());
                }

                accumulatedEvents.clear();
            }
        }
	}
}