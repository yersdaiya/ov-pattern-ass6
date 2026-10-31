package airport.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ListenerRegistry<E extends Event> {

    private final Map<EventType, List<Observer<? super E>>> listeners = new ConcurrentHashMap<>();

    public void subscribe(EventType type, Observer<? super E> observer) {
        listeners.computeIfAbsent(type, t -> new CopyOnWriteArrayList<>()).add(observer);
    }

    public void unsubscribe(EventType type, Observer<? super E> observer) {
        List<Observer<? super E>> forType = listeners.get(type);
        if (forType != null) {
            forType.remove(observer);
        }
    }

    public void publish(E event) {
        List<Observer<? super E>> forType = listeners.get(event.type());
        if (forType == null) {
            return;
        }
        for (Observer<? super E> observer : forType) {
            observer.onEvent(event);
        }
    }
}
