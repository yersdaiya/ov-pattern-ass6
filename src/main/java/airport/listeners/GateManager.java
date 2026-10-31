package airport.listeners;

import airport.core.EventType;
import airport.core.ListenerRegistry;
import airport.core.Observer;
import airport.core.Subject;
import airport.events.AirportEvent;
import airport.events.FlightCancelled;
import airport.events.GateChanged;
import airport.events.GateOccupancyUpdated;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GateManager implements Observer<AirportEvent>, Subject<AirportEvent> {

    private final Map<String, String> gateByFlight = new ConcurrentHashMap<>();
    private final ListenerRegistry<AirportEvent> outgoing;

    public GateManager(ListenerRegistry<AirportEvent> outgoing) {
        this.outgoing = outgoing;
    }

    @Override
    public void onEvent(AirportEvent event) {
        if (event instanceof GateChanged e) {
            gateByFlight.put(e.flightId(), e.newGate());
            outgoing.publish(new GateOccupancyUpdated(e.flightId(), e.oldGate(), false));
            outgoing.publish(new GateOccupancyUpdated(e.flightId(), e.newGate(), true));
        } else if (event instanceof FlightCancelled e) {
            String freed = gateByFlight.remove(e.flightId());
            if (freed != null) {
                outgoing.publish(new GateOccupancyUpdated(e.flightId(), freed, false));
            }
        }
    }

    public String gateOf(String flightId) {
        return gateByFlight.get(flightId);
    }

    @Override
    public void subscribe(EventType type, Observer<? super AirportEvent> observer) {
        outgoing.subscribe(type, observer);
    }

    @Override
    public void unsubscribe(EventType type, Observer<? super AirportEvent> observer) {
        outgoing.unsubscribe(type, observer);
    }
}