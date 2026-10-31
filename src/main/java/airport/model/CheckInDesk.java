package airport.model;

public record CheckInDesk(String id, int passengersProcessed) implements AirportElement {

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}