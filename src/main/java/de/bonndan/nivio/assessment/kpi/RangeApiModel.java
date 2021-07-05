package de.bonndan.nivio.assessment.kpi;

import org.apache.commons.lang3.Range;
import org.springframework.lang.NonNull;

import java.util.Objects;

public class RangeApiModel {

    private final Range<Double> range;

    public RangeApiModel(@NonNull final Range<Double> range) {
        this.range = Objects.requireNonNull(range, "Non null range is required");
    }

    public String getMinimum() {
        return range.getMinimum().toString();
    }

    public String getMaximum() {
        return range.getMaximum().toString();
    }
}
