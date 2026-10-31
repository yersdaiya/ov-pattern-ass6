package airport.model;

public record Lounge(String name, double dailyRevenue, int visitors) implements AirportElement {

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}