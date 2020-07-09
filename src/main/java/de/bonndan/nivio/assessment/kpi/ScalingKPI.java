package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.model.Label;
import org.apache.commons.lang3.Range;

import java.util.Map;

/**
 * This KPI evaluates the scale label.
 */
public class ScalingKPI extends CustomKPI {

    public static final String IDENTIFIER = "scaling";

    public ScalingKPI() {
        label = Label.scale.name();
        ranges = Map.of(
                Status.GREEN, Range.between(2d, 10d),
                Status.YELLOW, Range.between(0d, 1d),
                Status.RED, Range.between(0d, 0d)
        );
    }

}
