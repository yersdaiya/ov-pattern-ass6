package airport.listeners;

import airport.core.Observer;
import airport.events.AirportEvent;
import airport.events.FlightDelayed;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DepartureBoard implements Observer<AirportEvent> {

    private final Map<String, Integer> delaysByFlight = new ConcurrentHashMap<>();

    @Override
    public void onEvent(AirportEvent event) {
        if (event instanceof FlightDelayed delayed) {
            delaysByFlight.put(delayed.flightId(), delayed.delayMinutes());
        }
    }

    public int delayOf(String flightId) {
        return delaysByFlight.getOrDefault(flightId, 0);
    }
}
