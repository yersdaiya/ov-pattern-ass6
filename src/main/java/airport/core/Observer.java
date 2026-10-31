package airport.core;

public interface Observer<E extends Event> {
    void onEvent(E event);
}
