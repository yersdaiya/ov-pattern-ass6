package airport.model;

public record SecurityCheckpoint(String id, int passengersScreened) implements AirportElement {

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}