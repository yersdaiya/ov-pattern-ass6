package airport;

import airport.core.EventType;
import airport.core.ListenerRegistry;
import airport.events.AirportEvent;
import airport.events.BaggageLoaded;
import airport.events.FlightCancelled;
import airport.events.FlightDelayed;
import airport.events.GateChanged;
import airport.feeds.BaggageFeed;
import airport.feeds.FlightStatusFeed;
import airport.feeds.ParallelPublisher;
import airport.listeners.BaggageSystem;
import airport.listeners.DelayImpactAnalyzer;
import airport.listeners.DelayStatistics;
import airport.listeners.DepartureBoard;
import airport.listeners.GateManager;
import airport.listeners.PassengerSmsNotifier;
import airport.model.Airport;
import airport.model.CheckInDesk;
import airport.model.Flight;
import airport.model.Gate;
import airport.model.Lounge;
import airport.model.Shop;
import airport.model.Terminal;

import java.util.List;

/** Composition root: the only place where objects are created and wired. */
public final class Main {

    public static void main(String[] args) {
        Airport airport = buildSampleAirport();

        FlightStatusFeed flightFeed = new FlightStatusFeed(new ListenerRegistry<>());
        BaggageFeed baggageFeed = new BaggageFeed(new ListenerRegistry<>());

        DepartureBoard board = new DepartureBoard();
        PassengerSmsNotifier sms = new PassengerSmsNotifier(msg -> System.out.println("[SMS] " + msg));
        GateManager gateManager = new GateManager(new ListenerRegistry<>());
        BaggageSystem baggage = new BaggageSystem();
        DelayStatistics stats = new DelayStatistics();
        DelayImpactAnalyzer analyzer = new DelayImpactAnalyzer(airport, new ListenerRegistry<>());

        flightFeed.subscribe(EventType.FLIGHT_DELAYED, board);
        flightFeed.subscribe(EventType.FLIGHT_DELAYED, sms);
        flightFeed.subscribe(EventType.FLIGHT_DELAYED, stats);
        flightFeed.subscribe(EventType.FLIGHT_DELAYED, analyzer);
        flightFeed.subscribe(EventType.FLIGHT_CANCELLED, sms);
        flightFeed.subscribe(EventType.FLIGHT_CANCELLED, gateManager);
        flightFeed.subscribe(EventType.FLIGHT_CANCELLED, baggage);
        flightFeed.subscribe(EventType.GATE_CHANGED, sms);
        flightFeed.subscribe(EventType.GATE_CHANGED, gateManager);
        baggageFeed.subscribe(EventType.BAGGAGE_LOADED, baggage);

        gateManager.subscribe(EventType.GATE_OCCUPANCY_UPDATED,
                (AirportEvent e) -> System.out.println("[GATE] " + e));
        analyzer.subscribe(EventType.TERMINAL_IMPACT_ASSESSED,
                (AirportEvent e) -> System.out.println("[IMPACT] " + e));

        flightFeed.publish(new FlightDelayed("KC101", 30));
        flightFeed.publish(new GateChanged("KC101", "A1", "B2"));
        baggageFeed.publish(new BaggageLoaded("KC101", 120));
        flightFeed.publish(new FlightCancelled("KC101", "weather"));

        int producerThreads = Integer.getInteger("producer.threads", 3);
        try (ParallelPublisher publisher = new ParallelPublisher(flightFeed, producerThreads)) {
            for (int p = 0; p < producerThreads; p++) {
                publisher.publishAsync(List.of(new FlightDelayed("KC20" + p, 5)));
            }
        }

        System.out.println("Delays: " + stats.delayCount() + ", total minutes: " + stats.totalDelayMinutes());
        System.out.println("Bags for KC101 after cancel: " + baggage.bagsOf("KC101"));
    }

    private static Airport buildSampleAirport() {
        Gate a1 = new Gate("A1", List.of(new Flight("KC101", 150, 30), new Flight("KC102", 120, 0)));
        Gate b1 = new Gate("B1", List.of(new Flight("KC201", 200, 15)));
        Terminal t1 = new Terminal("T1", List.of(
                a1, new Shop("DutyFree", 5000.0), new Lounge("Priority", 2000.0, 40), new CheckInDesk("D1", 300)));
        Terminal t2 = new Terminal("T2", List.of(b1, new Shop("Cafe", 1500.5)));
        return new Airport("Astana", List.of(t1, t2));
    }
}