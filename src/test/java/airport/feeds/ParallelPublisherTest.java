package airport.feeds;

import airport.core.EventType;
import airport.core.ListenerRegistry;
import airport.core.Observer;
import airport.events.AirportEvent;
import airport.events.FlightDelayed;
import airport.listeners.DelayStatistics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParallelPublisherTest {

    private static final int THREADS = 4;
    private static final int EVENTS_PER_THREAD = 1000;

    private final FlightStatusFeed feed = new FlightStatusFeed(new ListenerRegistry<>());
    private final DelayStatistics stats = new DelayStatistics();

    private static List<AirportEvent> batchOfDelays(int count) {
        List<AirportEvent> batch = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            batch.add(new FlightDelayed("KC" + i, 1));
        }
        return batch;
    }

    @Test
    void stressFourThreadsTimesThousandEventsAreAllDelivered() throws Exception {
        feed.subscribe(EventType.FLIGHT_DELAYED, stats);
        List<Future<?>> futures = new ArrayList<>();

        try (ParallelPublisher publisher = new ParallelPublisher(feed, THREADS)) {
            for (int t = 0; t < THREADS; t++) {
                futures.add(publisher.publishAsync(batchOfDelays(EVENTS_PER_THREAD)));
            }
            for (Future<?> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }
        }

        assertEquals(4000, stats.delayCount());
        assertEquals(4000, stats.totalDelayMinutes());
    }

    @Test
    void gracefulCloseWaitsForPendingBatchesWithoutLosingEvents() {
        feed.subscribe(EventType.FLIGHT_DELAYED, stats);

        try (ParallelPublisher publisher = new ParallelPublisher(feed, THREADS)) {
            for (int t = 0; t < THREADS; t++) {
                publisher.publishAsync(batchOfDelays(EVENTS_PER_THREAD));
            }
        }

        assertEquals(4000, stats.delayCount());
    }

    @Test
    void unsubscribingWhilePublishingDoesNotFailOrAffectOtherListeners() throws Exception {
        feed.subscribe(EventType.FLIGHT_DELAYED, stats);
        AtomicInteger seenByTemporary = new AtomicInteger();
        Observer<AirportEvent> temporary = e -> seenByTemporary.incrementAndGet();
        feed.subscribe(EventType.FLIGHT_DELAYED, temporary);
        List<Future<?>> futures = new ArrayList<>();

        try (ParallelPublisher publisher = new ParallelPublisher(feed, THREADS)) {
            for (int t = 0; t < THREADS; t++) {
                futures.add(publisher.publishAsync(batchOfDelays(EVENTS_PER_THREAD)));
            }
            feed.unsubscribe(EventType.FLIGHT_DELAYED, temporary);
            for (Future<?> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }
        }

        assertEquals(4000, stats.delayCount());
        assertTrue(seenByTemporary.get() <= 4000);
    }
}