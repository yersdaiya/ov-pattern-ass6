package airport.visitors;

import airport.model.Airport;
import airport.model.CheckInDesk;
import airport.model.Flight;
import airport.model.Gate;
import airport.model.Lounge;
import airport.model.Shop;
import airport.model.Terminal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VisitorsTest {

    private static Airport sampleAirport() {
        Gate a1 = new Gate("A1", List.of(new Flight("KC101", 150, 30), new Flight("KC102", 120, 0)));
        Gate b1 = new Gate("B1", List.of(new Flight("KC201", 200, 15)));
        Terminal t1 = new Terminal("T1", List.of(
                a1, new Shop("DutyFree", 5000.0), new Lounge("Priority", 2000.0, 40), new CheckInDesk("D1", 300)));
        Terminal t2 = new Terminal("T2", List.of(b1, new Shop("Cafe", 1500.5)));
        return new Airport("Astana", List.of(t1, t2));
    }

    @Test
    void revenueVisitorSumsShopsAndLounges() {
        double revenue = sampleAirport().accept(new RevenueVisitor());
        assertEquals(8500.5, revenue, 1e-9);
    }

    @Test
    void revenueVisitorOnSubtreeOnlyCountsThatSubtree() {
        Airport airport = sampleAirport();
        double t2Revenue = airport.terminals().get(1).accept(new RevenueVisitor());
        assertEquals(1500.5, t2Revenue, 1e-9);
    }

    @Test
    void passengerLoadVisitorAccumulatesPerTerminal() {
        PassengerLoadVisitor visitor = new PassengerLoadVisitor();
        sampleAirport().accept(visitor);

        assertEquals(470, visitor.totalPassengers());
        assertEquals(Map.of("T1", 270, "T2", 200), visitor.passengersByTerminal());
    }

    @Test
    void delayedFlightsFilterReturnsOnlyDelayedFlights() {
        List<Flight> delayed = sampleAirport().accept(new DelayedFlightsFilter());
        assertEquals(List.of(new Flight("KC101", 150, 30), new Flight("KC201", 200, 15)), delayed);
    }

    @Test
    void jsonExportOfEmptyAirport() {
        String json = new Airport("X", List.of()).accept(new JsonExportVisitor());
        assertEquals("{\"type\":\"airport\",\"name\":\"X\",\"terminals\":[]}", json);
    }

    @Test
    void jsonExportOfFlightLeaf() {
        String json = new Flight("KC101", 150, 30).accept(new JsonExportVisitor());
        assertEquals("{\"type\":\"flight\",\"id\":\"KC101\",\"passengers\":150,\"delayMinutes\":30}", json);
    }

    @Test
    void jsonExportEscapesQuotes() {
        String json = new Shop("Joe's \"Best\" Cafe", 10.0).accept(new JsonExportVisitor());
        assertEquals("{\"type\":\"shop\",\"name\":\"Joe's \\\"Best\\\" Cafe\",\"dailyRevenue\":10.0}", json);
    }
}