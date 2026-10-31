package airport.events;

import airport.core.EventType;

public record BaggageLoaded(String flightId, int bagCount) implements AirportEvent {
    @Override
    public EventType type() {
        return EventType.BAGGAGE_LOADED;
    }
}