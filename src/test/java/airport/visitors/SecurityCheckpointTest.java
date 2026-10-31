package airport.visitors;

import airport.model.Flight;
import airport.model.Gate;
import airport.model.SecurityCheckpoint;
import airport.model.Shop;
import airport.model.Terminal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityCheckpointTest {

    @Test
    void jsonExportContainsCheckpoint() {
        String json = new SecurityCheckpoint("S1", 500).accept(new JsonExportVisitor());

        assertEquals("{\"type\":\"securityCheckpoint\",\"id\":\"S1\",\"passengersScreened\":500}", json);
    }

    @Test
    void jsonExportNestsCheckpointInsideTerminal() {
        Terminal terminal = new Terminal("T1", List.of(new SecurityCheckpoint("S1", 500)));

        String json = terminal.accept(new JsonExportVisitor());

        assertEquals("{\"type\":\"terminal\",\"name\":\"T1\",\"children\":"
                + "[{\"type\":\"securityCheckpoint\",\"id\":\"S1\",\"passengersScreened\":500}]}", json);
    }

    @Test
    void checkpointDoesNotChangeRevenueOrDelayedFlights() {
        Terminal terminal = new Terminal("T1", List.of(
                new Shop("Cafe", 100.0),
                new SecurityCheckpoint("S1", 500),
                new Gate("A1", List.of(new Flight("KC101", 150, 30)))));

        assertEquals(100.0, terminal.accept(new RevenueVisitor()), 1e-9);
        assertEquals(List.of(new Flight("KC101", 150, 30)), terminal.accept(new DelayedFlightsFilter()));
    }
}