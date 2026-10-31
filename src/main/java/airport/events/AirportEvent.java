package airport.events;

import airport.core.Event;

public interface AirportEvent extends Event {
    String flightId();
}
