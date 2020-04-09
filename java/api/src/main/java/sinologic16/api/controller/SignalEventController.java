package sinologic16.api.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sinologic16.api.model.SignalEvent;
import sinologic16.api.SignalHandler;

@RestController
public class SignalEventController {

    @Resource(name = "signalHandler")
    public SignalHandler signalHandler;

    @RequestMapping("api/")
    public String get() {
        String response = "From SignalEventController.get()" + signalHandler.exchange;
        signalHandler.exchange = "exchange is get";

        return response;
    }

    @PostMapping("api/")
    SignalEvent newSignalEvent(@RequestBody SignalEvent newSignalEvent) {
        return newSignalEvent;
    }
}