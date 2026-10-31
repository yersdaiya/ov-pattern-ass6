package airport.listeners;

import airport.core.Observer;
import airport.events.AirportEvent;
import airport.events.FlightBoarding;
import airport.events.FlightCancelled;
import airport.events.FlightDelayed;
import airport.events.GateChanged;

import java.util.function.Consumer;

public final class PassengerSmsNotifier implements Observer<AirportEvent> {

    private final Consumer<String> smsSink;

    public PassengerSmsNotifier(Consumer<String> smsSink) {
        this.smsSink = smsSink;
    }

    @Override
    public void onEvent(AirportEvent event) {
        if (event instanceof FlightDelayed e) {
            smsSink.accept("Flight " + e.flightId() + " delayed by " + e.delayMinutes() + " min");
        } else if (event instanceof FlightCancelled e) {
            smsSink.accept("Flight " + e.flightId() + " cancelled: " + e.reason());
        } else if (event instanceof FlightBoarding e) {
            smsSink.accept("Flight " + e.flightId() + " boarding at gate " + e.gate());
        } else if (event instanceof GateChanged e) {
            smsSink.accept("Flight " + e.flightId() + " gate changed " + e.oldGate() + " -> " + e.newGate());
        }
    }
}