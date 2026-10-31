package airport.events;

import airport.core.EventType;

public record FlightBoarding(String flightId, String gate) implements AirportEvent {
    @Override
    public EventType type() {
        return EventType.FLIGHT_BOARDING;
    }
}