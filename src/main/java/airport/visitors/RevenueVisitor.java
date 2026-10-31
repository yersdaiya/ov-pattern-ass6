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

/** Total daily revenue of shops and lounges in the visited subtree. */
public final class RevenueVisitor implements Visitor<Double> {

    @Override
    public Double visit(Airport airport) {
        return sum(airport.terminals());
    }

    @Override
    public Double visit(Terminal terminal) {
        return sum(terminal.children());
    }

    @Override
    public Double visit(Gate gate) {
        return sum(gate.flights());
    }

    @Override
    public Double visit(Flight flight) {
        return 0.0;
    }

    @Override
    public Double visit(Lounge lounge) {
        return lounge.dailyRevenue();
    }

    @Override
    public Double visit(Shop shop) {
        return shop.dailyRevenue();
    }

    @Override
    public Double visit(CheckInDesk desk) {
        return 0.0;
    }

    @Override
    public Double visit(SecurityCheckpoint checkpoint) {
        return 0.0;
    }

    private double sum(List<? extends AirportElement> elements) {
        double total = 0;
        for (AirportElement element : elements) {
            total += element.accept(this);
        }
        return total;
    }
}