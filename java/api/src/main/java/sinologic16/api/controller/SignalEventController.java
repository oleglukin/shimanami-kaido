package sinologic16.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sinologic16.api.model.SignalEvent;

@RestController
public class SignalEventController {

    @RequestMapping("api/")
    public String get() {
        return "From SignalEventController.get()";
    }

    @PostMapping("api/")
    SignalEvent newSignalEvent(@RequestBody SignalEvent newSignalEvent) {
        return newSignalEvent;
    }
}