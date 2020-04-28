package de.bonndan.nivio.assessment.kpi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Labeled;
import org.apache.commons.lang3.Range;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A configured key performance indicator related to a landscape item label.
 */
public class CustomKPI extends KPI {

    public static final String SEPARATOR = ";";

    /**
     * For each {@link Status} a numeric range can be defined (includes lower/upper limit).
     */
    private final Map<Status, Range<Double>> ranges;

    /**
     * For each {@link Status} a set of regular expressions can be defined which indicate the status.
     */
    private final Map<Status, List<Function<String, Boolean>>> matches = new HashMap<>();

    private final String label;

    /**
     * @param label        the label which is evaluated for status
     * @param messageLabel the label which is used as optional message
     */
    @JsonCreator
    public CustomKPI(@NonNull @JsonProperty(value = "label", required = true) String label,
                     @Nullable @JsonProperty("messageLabel") String messageLabel,
                     @Nullable @JsonProperty("ranges") Map<Status, String> ranges,
                     @Nullable @JsonProperty("matches") Map<Status, String> matches
    ) {
        super(component -> {
                    if (component instanceof Labeled) {
                        return ((Labeled) component).getLabel(label);
                    }
                    throw new RuntimeException("Custom KPIs can only evaluate labels (custom fields).");
                },
                component -> {
                    if (component instanceof Labeled) {
                        return ((Labeled) component).getLabel(messageLabel);
                    }
                    return null;
                });

        this.label = label;
        this.ranges = asRanges(ranges);
        addSpecs(matches);
    }

    @Override
    protected List<StatusValue> getStatusValues(String value, String message) {

        List<StatusValue> values = new ArrayList<>();
        for (Status status : Status.values()) {
            Optional<Status> statusByRange = getStatusByRange(value);
            if (statusByRange.isPresent()) {
                values.add(new StatusValue(label, statusByRange.get(), message));
                break;
            }

            if (!matches.containsKey(status)) {
                continue;
            }
            boolean anyMatch = matches.get(status).stream().anyMatch(stringBooleanFunction -> stringBooleanFunction.apply(value));
            if (anyMatch) {
                values.add(new StatusValue(label, status, message));
                break;
            }
        }

        return values;
    }

    private Map<Status, Range<Double>> asRanges(Map<Status, String> ranges) {
        Map<Status, Range<Double>> rangeMap = new HashMap<>();
        if (ranges == null) {
            return rangeMap;
        }

        ranges.forEach((status, s) -> {
            String[] split;
            if (s.contains(SEPARATOR)) {
                split = s.split(SEPARATOR);
            } else {
                split = new String[2];
                split[0] = s;
                split[1] = s;
            }
            try {
                rangeMap.put(status, Range.between(Double.valueOf(split[0]), Double.valueOf(split[1])));
            } catch (NumberFormatException e) {
                throw new ProcessingException("Failed to parse KPI range: " + s, e);
            }
        });
        return rangeMap;
    }

    private Optional<Status> getStatusByRange(String value) {
        if (value == null) {
            return Optional.empty();
        }

        double d;
        try {
            d = Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
        return ranges.entrySet().stream()
                .filter(entry -> entry.getValue().contains(d))
                .findFirst()
                .map(Map.Entry::getKey);
    }

    private void addSpecs(@Nullable Map<Status, String> statusPatterns) {
        if (statusPatterns == null)
            return;

        statusPatterns.forEach((status, strings) -> {
            List<Function<String, Boolean>> specs = Arrays.stream(strings.split(SEPARATOR)).map(s -> {
                try {
                    Pattern p = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                    return (Function<String, Boolean>) s1 -> p.matcher(s1).matches();
                } catch (Exception e) {
                    return (Function<String, Boolean>) s1 -> s1.contains(s);
                }

            }).collect(Collectors.toList());
            matches.put(status, specs);
        });
    }
}
