package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.model.Label;

import java.util.Map;

/**
 * This KPI evaluates the scale label.
 */
public class ScalingKPI extends CustomKPI {

    public static final String IDENTIFIER = "scaling";

    private static final Map<Status, String> ranges = Map.of(
            Status.GREEN, "2;10",
            Status.YELLOW, "1",
            Status.RED, "0"
    );

    public ScalingKPI() {
        super(Label.SCALE.toString().toLowerCase(), null, ranges, null);
    }

}
