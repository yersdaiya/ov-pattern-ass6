package airport.model;

public interface Visitor<R> {
    R visit(Airport airport);

    R visit(Terminal terminal);

    R visit(Gate gate);

    R visit(Flight flight);

    R visit(Lounge lounge);

    R visit(Shop shop);

    R visit(CheckInDesk desk);

    R visit(SecurityCheckpoint checkpoint);
}