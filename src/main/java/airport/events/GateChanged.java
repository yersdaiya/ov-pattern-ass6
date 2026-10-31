package airport.events;

import airport.core.EventType;

public record GateChanged(String flightId, String oldGate, String newGate) implements AirportEvent {
    @Override
    public EventType type() {
        return EventType.GATE_CHANGED;
    }
}
