package de.bonndan.nivio.input.demo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ChangeTrigger {

    private final PetClinicSimulator simulator;

    public ChangeTrigger(PetClinicSimulator simulator) {
        this.simulator = simulator;
    }

    @Scheduled(initialDelay = 20000, fixedDelay = 30000)
    public void trigger() {
        simulator.simulateSomething();
    }
}
