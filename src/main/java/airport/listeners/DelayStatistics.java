package airport.listeners;

import airport.core.Observer;
import airport.events.AirportEvent;
import airport.events.FlightDelayed;

import java.util.concurrent.atomic.LongAdder;

public final class DelayStatistics implements Observer<AirportEvent> {

    private final LongAdder delayCount = new LongAdder();
    private final LongAdder totalDelayMinutes = new LongAdder();

    @Override
    public void onEvent(AirportEvent event) {
        if (event instanceof FlightDelayed e) {
            delayCount.increment();
            totalDelayMinutes.add(e.delayMinutes());
        }
    }

    public long delayCount() {
        return delayCount.sum();
    }

    public long totalDelayMinutes() {
        return totalDelayMinutes.sum();
    }
}