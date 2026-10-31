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

import java.util.ArrayList;
import java.util.List;

public final class DelayedFlightsFilter implements Visitor<List<Flight>> {

    @Override
    public List<Flight> visit(Airport airport) {
        return collect(airport.terminals());
    }

    @Override
    public List<Flight> visit(Terminal terminal) {
        return collect(terminal.children());
    }

    @Override
    public List<Flight> visit(Gate gate) {
        return collect(gate.flights());
    }

    @Override
    public List<Flight> visit(Flight flight) {
        return flight.delayMinutes() > 0 ? List.of(flight) : List.of();
    }

    @Override
    public List<Flight> visit(Lounge lounge) {
        return List.of();
    }

    @Override
    public List<Flight> visit(Shop shop) {
        return List.of();
    }

    @Override
    public List<Flight> visit(CheckInDesk desk) {
        return List.of();
    }

    @Override
    public List<Flight> visit(SecurityCheckpoint checkpoint) {
        return List.of();
    }

    private List<Flight> collect(List<? extends AirportElement> elements) {
        List<Flight> result = new ArrayList<>();
        for (AirportElement element : elements) {
            result.addAll(element.accept(this));
        }
        return result;
    }
}