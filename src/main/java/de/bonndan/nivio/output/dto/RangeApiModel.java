package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.Range;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RangeApiModel {

    @NonNull
    private final Range<Double> range;

    @Nullable
    private final String description;

    public RangeApiModel(@NonNull final Range<Double> range) {
        this(range, null);
    }

    public RangeApiModel(@NonNull final Range<Double> range, @Nullable final String description) {
        this.range = Objects.requireNonNull(range, "Non null range is required");
        this.description = description;
    }

    @NonNull
    public String getMinimum() {
        return range.getMinimum().toString();
    }

    @NonNull
    public String getMaximum() {
        return range.getMaximum().toString();
    }

    @Nullable
    public String getDescription() {
        return description;
    }
}
