package airport.visitors;

import airport.model.Airport;
import airport.model.AirportElement;
import airport.model.CheckInDesk;
import airport.model.Flight;
import airport.model.Gate;
import airport.model.Lounge;
import airport.model.SecurityCheckpoint;
import airport.model.Shop;
import airport.model.Terminal;
import airport.model.Visitor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PassengerLoadVisitor implements Visitor<Void> {

    private final Map<String, Integer> byTerminal = new LinkedHashMap<>();
    private String currentTerminal = "(none)";
    private int total;

    @Override
    public Void visit(Airport airport) {
        visitAll(airport.terminals());
        return null;
    }

    @Override
    public Void visit(Terminal terminal) {
        String previous = currentTerminal;
        currentTerminal = terminal.name();
        byTerminal.putIfAbsent(currentTerminal, 0);
        visitAll(terminal.children());
        currentTerminal = previous;
        return null;
    }

    @Override
    public Void visit(Gate gate) {
        visitAll(gate.flights());
        return null;
    }

    @Override
    public Void visit(Flight flight) {
        total += flight.passengers();
        byTerminal.merge(currentTerminal, flight.passengers(), Integer::sum);
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

    @Override
    public Void visit(SecurityCheckpoint checkpoint) {
        return null;
    }

    public int totalPassengers() {
        return total;
    }

    public Map<String, Integer> passengersByTerminal() {
        return Collections.unmodifiableMap(byTerminal);
    }

    private void visitAll(List<? extends AirportElement> elements) {
        for (AirportElement element : elements) {
            element.accept(this);
        }
    }
}