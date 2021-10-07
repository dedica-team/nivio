package de.bonndan.nivio.observation;

import java.util.Map;

public class ObserverConfig {

    private Map<String, Integer> delay;

    public ObserverConfig() {
    }

    public ObserverConfig(Map<String, Integer> delay) {
        this.delay = delay;
    }

    public Map<String, Integer> getDelay() {
        return delay;
    }
}
