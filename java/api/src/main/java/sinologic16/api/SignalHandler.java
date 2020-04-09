package sinologic16.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SignalHandler {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    public String exchange = "default exchange";
    
    @Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		System.out.printf("The time is now " + dateFormat.format(new Date()) + "\t'" + exchange + "'\n");
	}
}