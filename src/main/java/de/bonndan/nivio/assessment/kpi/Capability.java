package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.model.Label;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Capability extends CustomKPI {

    private static Map<Status, String> matches = new HashMap<>();
    static {
        Arrays.stream(Status.values()).forEach(status -> matches.put(status, status.name().toLowerCase()));
    }

    public Capability() {
        super(Label.CAPABILITY.name().toLowerCase(), null, null, matches);
    }
}
