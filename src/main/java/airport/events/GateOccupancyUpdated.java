package airport.events;

import airport.core.EventType;

public record GateOccupancyUpdated(String flightId, String gate, boolean occupied) implements AirportEvent {
    @Override
    public EventType type() {
        return EventType.GATE_OCCUPANCY_UPDATED;
    }
}