package airport.feeds;

import airport.events.AirportEvent;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class ParallelPublisher implements AutoCloseable {

    private static final long SHUTDOWN_TIMEOUT_SECONDS = 30;

    private final FlightStatusFeed feed;
    private final ExecutorService executor;

    public ParallelPublisher(FlightStatusFeed feed, int threadCount) {
        this.feed = feed;
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    public Future<?> publishAsync(List<? extends AirportEvent> batch) {
        return executor.submit(() -> batch.forEach(feed::publish));
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                throw new IllegalStateException("Producers did not finish in time");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}