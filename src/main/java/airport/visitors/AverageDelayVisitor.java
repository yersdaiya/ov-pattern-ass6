package airport.visitors;

import airport.model.Airport;
import airport.model.AirportElement;
import airport.model.CheckInDesk;
import airport.model.Flight;
import airport.model.Gate;
import airport.model.Lounge;
import airport.model.Shop;
import airport.model.Terminal;
import airport.model.Visitor;

import java.util.List;

public final class AverageDelayVisitor implements Visitor<Void> {

    private int flightCount;
    private long totalDelayMinutes;

    @Override
    public Void visit(Airport airport) {
        visitAll(airport.terminals());
        return null;
    }

    @Override
    public Void visit(Terminal terminal) {
        visitAll(terminal.children());
        return null;
    }

    @Override
    public Void visit(Gate gate) {
        visitAll(gate.flights());
        return null;
    }

    @Override
    public Void visit(Flight flight) {
        flightCount++;
        totalDelayMinutes += flight.delayMinutes();
        return null;
    }

    @Override
    public Void visit(Lounge lounge) {
        return null;
    }

    @Override
    public Void visit(Shop shop) {
        return null;
    }

    @Override
    public Void visit(CheckInDesk desk) {
        return null;
    }

    public double averageDelayMinutes() {
        return flightCount == 0 ? 0.0 : (double) totalDelayMinutes / flightCount;
    }

    public int flightCount() {
        return flightCount;
    }

    private void visitAll(List<? extends AirportElement> elements) {
        for (AirportElement element : elements) {
            element.accept(this);
        }
    }
}