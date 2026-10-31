package airport.visitors;

import airport.model.Airport;
import airport.model.AirportElement;
import airport.model.CheckInDesk;
import airport.model.Flight;
import airport.model.Gate;
import airport.model.Lounge;
import airport.model.SecurityCheckpoint;
import airport.model.Shop;
import airport.model.Terminal;
import airport.model.Visitor;

import java.util.List;

public final class JsonExportVisitor implements Visitor<String> {

    @Override
    public String visit(Airport airport) {
        return "{\"type\":\"airport\",\"name\":" + quote(airport.name())
                + ",\"terminals\":" + array(airport.terminals()) + "}";
    }

    @Override
    public String visit(Terminal terminal) {
        return "{\"type\":\"terminal\",\"name\":" + quote(terminal.name())
                + ",\"children\":" + array(terminal.children()) + "}";
    }

    @Override
    public String visit(Gate gate) {
        return "{\"type\":\"gate\",\"code\":" + quote(gate.code())
                + ",\"flights\":" + array(gate.flights()) + "}";
    }

    @Override
    public String visit(Flight flight) {
        return "{\"type\":\"flight\",\"id\":" + quote(flight.id())
                + ",\"passengers\":" + flight.passengers()
                + ",\"delayMinutes\":" + flight.delayMinutes() + "}";
    }

    @Override
    public String visit(Lounge lounge) {
        return "{\"type\":\"lounge\",\"name\":" + quote(lounge.name())
                + ",\"dailyRevenue\":" + lounge.dailyRevenue()
                + ",\"visitors\":" + lounge.visitors() + "}";
    }

    @Override
    public String visit(Shop shop) {
        return "{\"type\":\"shop\",\"name\":" + quote(shop.name())
                + ",\"dailyRevenue\":" + shop.dailyRevenue() + "}";
    }

    @Override
    public String visit(CheckInDesk desk) {
        return "{\"type\":\"checkInDesk\",\"id\":" + quote(desk.id())
                + ",\"passengersProcessed\":" + desk.passengersProcessed() + "}";
    }

    @Override
    public String visit(SecurityCheckpoint checkpoint) {
        return "{\"type\":\"securityCheckpoint\",\"id\":" + quote(checkpoint.id())
                + ",\"passengersScreened\":" + checkpoint.passengersScreened() + "}";
    }

    private String array(List<? extends AirportElement> elements) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(elements.get(i).accept(this));
        }
        return sb.append(']').toString();
    }

    private static String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}