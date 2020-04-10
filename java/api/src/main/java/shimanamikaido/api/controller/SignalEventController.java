package shimanamikaido.api.controller;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import shimanamikaido.api.model.LocationAggregation;
import shimanamikaido.api.model.SignalEvent;
import shimanamikaido.api.SignalHandler;

@RestController
public class SignalEventController {

    @Resource(name = "signalHandler")
    public SignalHandler signalHandler;

    // maps id_location to aggregation of counts (functional, failed)
    private ConcurrentHashMap<String, LocationAggregation> map = new ConcurrentHashMap<String, LocationAggregation>();

    /**
     * Get list of all locations that have aggregations
     */
    @RequestMapping("api/all")
    public String getLocations() {
        StringBuilder response = new StringBuilder();

        map.forEach((k, v) -> response.append(k+ ". " + "Functional: " + v.getFunctional() + ", failed: " + v.getFailed() + "\n"));
  
        return response.toString();
    }


    /**
     * Get aggregation by idLocation
     * @param idLocation
     * @return string like "Functional: 22641, failed: 1842"
     */
    @RequestMapping("api/")
    public String get(@RequestParam("id_location") String idLocation) {
        String response;

        LocationAggregation agg = map.get(idLocation);
        if (agg != null) {
            response = "Functional: " + agg.getFunctional() + ", failed: " + agg.getFailed();
        } else {
            response = "No aggregations found for '" + idLocation + "'";
        }

        return response;
    }

    /**
     * Receive new signal, send it to processing
     * @param newSignalEvent
     */
    @PostMapping("api/")
    public void newSignalEvent(@RequestBody SignalEvent newSignalEvent) {
        signalHandler.addEvent(newSignalEvent);
    }

    /**
     * Get aggregation, update or add it to the map
     */
    @PostMapping("api/updateaggregation")
    void newAggregation(
        @RequestParam("id_location") String idLocation,
        @RequestParam("id_detected") String idDetected,
        @RequestParam("count") int count
        ) {

        LocationAggregation agg = map.get(idLocation);
        if (agg == null) {
            agg = new LocationAggregation();
        }

        if (idDetected.equals("None")) {
            agg.setFunctional(count);
        }
        else if (idDetected.equals("Nan")) {
            agg.setFailed(count);
        }

        map.put(idLocation, agg);
    }
}