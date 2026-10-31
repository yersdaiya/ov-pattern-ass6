package airport.events;

import airport.core.EventType;

public record FlightCancelled(String flightId, String reason) implements AirportEvent {
    @Override
    public EventType type() {
        return EventType.FLIGHT_CANCELLED;
    }
}