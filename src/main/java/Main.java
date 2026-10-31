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
import airport.listeners.BaggageSystem;
import airport.listeners.DelayStatistics;
import airport.listeners.DepartureBoard;
import airport.listeners.GateManager;
import airport.listeners.PassengerSmsNotifier;

public final class Main {

    public static void main(String[] args) {
        FlightStatusFeed flightFeed = new FlightStatusFeed(new ListenerRegistry<>());
        BaggageFeed baggageFeed = new BaggageFeed(new ListenerRegistry<>());

        DepartureBoard board = new DepartureBoard();
        PassengerSmsNotifier sms = new PassengerSmsNotifier(msg -> System.out.println("[SMS] " + msg));
        GateManager gateManager = new GateManager(new ListenerRegistry<>());
        BaggageSystem baggage = new BaggageSystem();
        DelayStatistics stats = new DelayStatistics();

        flightFeed.subscribe(EventType.FLIGHT_DELAYED, board);
        flightFeed.subscribe(EventType.FLIGHT_DELAYED, sms);
        flightFeed.subscribe(EventType.FLIGHT_DELAYED, stats);
        flightFeed.subscribe(EventType.FLIGHT_CANCELLED, sms);
        flightFeed.subscribe(EventType.FLIGHT_CANCELLED, gateManager);
        flightFeed.subscribe(EventType.FLIGHT_CANCELLED, baggage);
        flightFeed.subscribe(EventType.GATE_CHANGED, sms);
        flightFeed.subscribe(EventType.GATE_CHANGED, gateManager);
        baggageFeed.subscribe(EventType.BAGGAGE_LOADED, baggage);

        gateManager.subscribe(EventType.GATE_OCCUPANCY_UPDATED,
                (AirportEvent e) -> System.out.println("[GATE] " + e));

        flightFeed.publish(new FlightDelayed("KC101", 30));
        flightFeed.publish(new GateChanged("KC101", "A1", "B2"));
        baggageFeed.publish(new BaggageLoaded("KC101", 120));
        flightFeed.publish(new FlightCancelled("KC101", "weather"));

        System.out.println("Delays: " + stats.delayCount() + ", total minutes: " + stats.totalDelayMinutes());
        System.out.println("Bags for KC101 after cancel: " + baggage.bagsOf("KC101"));
    }
}