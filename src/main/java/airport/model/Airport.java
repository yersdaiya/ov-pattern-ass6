package airport.model;

import java.util.List;

public record Airport(String name, List<Terminal> terminals) implements AirportElement {

    public Airport {
        terminals = List.copyOf(terminals);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}