package shimanamikaido.api.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shimanamikaido.api.model.SignalEvent;
import shimanamikaido.api.SignalHandler;

@RestController
public class SignalEventController {

    @Resource(name = "signalHandler")
    public SignalHandler signalHandler;

    @RequestMapping("api/")
    public String get() {
        String response = "From SignalEventController.get()";

        return response;
    }

    @PostMapping("api/")
    SignalEvent newSignalEvent(@RequestBody SignalEvent newSignalEvent) {
        signalHandler.addEvent(newSignalEvent);
        return newSignalEvent;
    }

    @PostMapping("api/updateaggregation")
    void newAggregation() {
        // TODO implement
    }
}