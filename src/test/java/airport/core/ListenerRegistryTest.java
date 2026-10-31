package airport.core;

import airport.events.AirportEvent;
import airport.events.FlightDelayed;
import airport.events.GateChanged;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListenerRegistryTest {

    private final ListenerRegistry<AirportEvent> registry = new ListenerRegistry<>();

    @Test
    void listenerReceivesOnlySubscribedEventType() {
        List<AirportEvent> received = new ArrayList<>();
        registry.subscribe(EventType.FLIGHT_DELAYED, received::add);

        registry.publish(new FlightDelayed("KC101", 30));
        registry.publish(new GateChanged("KC101", "A1", "B2"));

        assertEquals(1, received.size());
    }

    @Test
    void unsubscribedListenerReceivesNothing() {
        List<AirportEvent> received = new ArrayList<>();
        Observer<AirportEvent> listener = received::add;
        registry.subscribe(EventType.FLIGHT_DELAYED, listener);
        registry.unsubscribe(EventType.FLIGHT_DELAYED, listener);

        registry.publish(new FlightDelayed("KC101", 30));

        assertEquals(0, received.size());
    }

    @Test
    void listenerCanUnsubscribeItselfInsideHandler() {
        List<AirportEvent> received = new ArrayList<>();
        Observer<AirportEvent> oneShot = new Observer<>() {
            @Override
            public void onEvent(AirportEvent event) {
                received.add(event);
                registry.unsubscribe(EventType.FLIGHT_DELAYED, this);
            }
        };
        registry.subscribe(EventType.FLIGHT_DELAYED, oneShot);

        registry.publish(new FlightDelayed("KC101", 10));
        registry.publish(new FlightDelayed("KC101", 20));

        assertEquals(1, received.size());
    }
}
