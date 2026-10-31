package airport.listeners;

import airport.core.Observer;
import airport.events.AirportEvent;
import airport.events.BaggageLoaded;
import airport.events.FlightCancelled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BaggageSystem implements Observer<AirportEvent> {

    private final Map<String, Integer> bagsByFlight = new ConcurrentHashMap<>();

    @Override
    public void onEvent(AirportEvent event) {
        if (event instanceof BaggageLoaded e) {
            bagsByFlight.merge(e.flightId(), e.bagCount(), Integer::sum);
        } else if (event instanceof FlightCancelled e) {
            bagsByFlight.remove(e.flightId());
        }
    }

    public int bagsOf(String flightId) {
        return bagsByFlight.getOrDefault(flightId, 0);
    }
}