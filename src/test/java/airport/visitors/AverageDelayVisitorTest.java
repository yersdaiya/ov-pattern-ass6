package airport.visitors;

import airport.model.Airport;
import airport.model.Flight;
import airport.model.Gate;
import airport.model.Shop;
import airport.model.Terminal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AverageDelayVisitorTest {

    @Test
    void averagesDelaysOfAllFlightsInTree() {
        Gate g1 = new Gate("A1", List.of(new Flight("F1", 100, 10), new Flight("F2", 100, 20)));
        Gate g2 = new Gate("B1", List.of(new Flight("F3", 100, 60)));
        Airport airport = new Airport("X", List.of(
                new Terminal("T1", List.of(g1)),
                new Terminal("T2", List.of(g2, new Shop("Cafe", 10.0)))));

        AverageDelayVisitor visitor = new AverageDelayVisitor();
        airport.accept(visitor);

        assertEquals(3, visitor.flightCount());
        assertEquals(30.0, visitor.averageDelayMinutes(), 1e-9);
    }

    @Test
    void subtreeOnlyCountsItsOwnFlights() {
        Gate g1 = new Gate("A1", List.of(new Flight("F1", 100, 10), new Flight("F2", 100, 20)));
        Gate g2 = new Gate("B1", List.of(new Flight("F3", 100, 60)));
        Terminal t2 = new Terminal("T2", List.of(g2));
        Airport airport = new Airport("X", List.of(new Terminal("T1", List.of(g1)), t2));

        AverageDelayVisitor visitor = new AverageDelayVisitor();
        airport.terminals().get(1).accept(visitor);

        assertEquals(1, visitor.flightCount());
        assertEquals(60.0, visitor.averageDelayMinutes(), 1e-9);
    }

    @Test
    void treeWithoutFlightsHasZeroAverage() {
        AverageDelayVisitor visitor = new AverageDelayVisitor();
        new Airport("Empty", List.of()).accept(visitor);

        assertEquals(0, visitor.flightCount());
        assertEquals(0.0, visitor.averageDelayMinutes(), 1e-9);
    }
}