package airport.listeners;

import airport.core.EventType;
import airport.core.ListenerRegistry;
import airport.events.AirportEvent;
import airport.events.BaggageLoaded;
import airport.events.FlightCancelled;
import airport.events.FlightDelayed;
import airport.events.GateChanged;
import airport.events.GateOccupancyUpdated;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListenersTest {

    @Test
    void departureBoardKeepsLatestDelay() {
        DepartureBoard board = new DepartureBoard();
        board.onEvent(new FlightDelayed("KC101", 15));
        board.onEvent(new FlightDelayed("KC101", 45));
        assertEquals(45, board.delayOf("KC101"));
        assertEquals(0, board.delayOf("UNKNOWN"));
    }

    @Test
    void delayStatisticsSumsMinutes() {
        DelayStatistics stats = new DelayStatistics();
        stats.onEvent(new FlightDelayed("A", 10));
        stats.onEvent(new FlightDelayed("B", 20));
        assertEquals(2, stats.delayCount());
        assertEquals(30, stats.totalDelayMinutes());
    }

    @Test
    void baggageSystemAccumulatesAndClearsOnCancel() {
        BaggageSystem baggage = new BaggageSystem();
        baggage.onEvent(new BaggageLoaded("KC101", 50));
        baggage.onEvent(new BaggageLoaded("KC101", 70));
        assertEquals(120, baggage.bagsOf("KC101"));
        baggage.onEvent(new FlightCancelled("KC101", "weather"));
        assertEquals(0, baggage.bagsOf("KC101"));
    }

    @Test
    void smsNotifierFormatsDelayMessage() {
        List<String> sent = new ArrayList<>();
        PassengerSmsNotifier sms = new PassengerSmsNotifier(sent::add);
        sms.onEvent(new FlightDelayed("KC101", 30));
        assertEquals(List.of("Flight KC101 delayed by 30 min"), sent);
    }

    @Test
    void gateManagerChainPublishesFreedAndOccupiedGate() {
        GateManager manager = new GateManager(new ListenerRegistry<>());
        List<AirportEvent> downstream = new ArrayList<>();
        manager.subscribe(EventType.GATE_OCCUPANCY_UPDATED, downstream::add);

        manager.onEvent(new GateChanged("KC101", "A1", "B2"));

        assertEquals("B2", manager.gateOf("KC101"));
        assertEquals(List.of(
                new GateOccupancyUpdated("KC101", "A1", false),
                new GateOccupancyUpdated("KC101", "B2", true)), downstream);
    }

    @Test
    void gateManagerFreesGateOnCancel() {
        GateManager manager = new GateManager(new ListenerRegistry<>());
        List<AirportEvent> downstream = new ArrayList<>();
        manager.subscribe(EventType.GATE_OCCUPANCY_UPDATED, downstream::add);
        manager.onEvent(new GateChanged("KC101", "A1", "B2"));
        downstream.clear();

        manager.onEvent(new FlightCancelled("KC101", "weather"));

        assertNull(manager.gateOf("KC101"));
        assertEquals(1, downstream.size());
        assertTrue(downstream.get(0) instanceof GateOccupancyUpdated g && !g.occupied());
    }
}