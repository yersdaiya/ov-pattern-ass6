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

import java.util.List;

public final class ContainsFlightVisitor implements Visitor<Boolean> {

    private final String flightId;

    public ContainsFlightVisitor(String flightId) {
        this.flightId = flightId;
    }

    @Override
    public Boolean visit(Airport airport) {
        return anyMatch(airport.terminals());
    }

    @Override
    public Boolean visit(Terminal terminal) {
        return anyMatch(terminal.children());
    }

    @Override
    public Boolean visit(Gate gate) {
        return anyMatch(gate.flights());
    }

    @Override
    public Boolean visit(Flight flight) {
        return flight.id().equals(flightId);
    }

    @Override
    public Boolean visit(Lounge lounge) {
        return false;
    }

    @Override
    public Boolean visit(Shop shop) {
        return false;
    }

    @Override
    public Boolean visit(CheckInDesk desk) {
        return false;
    }

    @Override
    public Boolean visit(SecurityCheckpoint checkpoint) {
        return false;
    }

    private boolean anyMatch(List<? extends AirportElement> elements) {
        for (AirportElement element : elements) {
            if (element.accept(this)) {
                return true;
            }
        }
        return false;
    }
}