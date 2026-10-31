package airport.feeds;

import airport.core.EventType;
import airport.core.ListenerRegistry;
import airport.core.Observer;
import airport.core.Subject;
import airport.events.AirportEvent;

public final class FlightStatusFeed implements Subject<AirportEvent> {

    private final ListenerRegistry<AirportEvent> registry;

    public FlightStatusFeed(ListenerRegistry<AirportEvent> registry) {
        this.registry = registry;
    }

    @Override
    public void subscribe(EventType type, Observer<? super AirportEvent> observer) {
        registry.subscribe(type, observer);
    }

    @Override
    public void unsubscribe(EventType type, Observer<? super AirportEvent> observer) {
        registry.unsubscribe(type, observer);
    }

    public void publish(AirportEvent event) {
        registry.publish(event);
    }
}
