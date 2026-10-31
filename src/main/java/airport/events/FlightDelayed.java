package airport.events;

import airport.core.EventType;

public record FlightDelayed(String flightId, int delayMinutes) implements AirportEvent {
    @Override
    public EventType type() {
        return EventType.FLIGHT_DELAYED;
    }
}
