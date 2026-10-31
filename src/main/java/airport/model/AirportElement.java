package airport.model;

public interface AirportElement {
    <R> R accept(Visitor<R> visitor);
}