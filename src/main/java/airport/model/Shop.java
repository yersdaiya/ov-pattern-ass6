package airport.model;

public record Shop(String name, double dailyRevenue) implements AirportElement {

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}