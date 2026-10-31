package airport.model;

public record Flight(String id, int passengers, int delayMinutes) implements AirportElement {

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}