package airport.model;

import java.util.List;

public record Terminal(String name, List<AirportElement> children) implements AirportElement {

    public Terminal {
        children = List.copyOf(children);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}