package airport.model;

import java.util.List;

public record Gate(String code, List<Flight> flights) implements AirportElement {

    public Gate {
        flights = List.copyOf(flights);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}