package de.bonndan.nivio.assessment.kpi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Labeled;
import org.apache.commons.lang3.Range;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A configured key performance indicator related to a landscape item label.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomKPI extends AbstractKPI {

    public static final String SEPARATOR = ";";

    /**
     * For each {@link Status} a numeric range can be defined (includes lower/upper limit).
     */
    protected Map<Status, Range<Double>> ranges;

    /**
     * For each {@link Status} a set of regular expressions can be defined which indicate the status.
     */
    private final Map<Status, List<Function<String, Boolean>>> matches = new HashMap<>();

    protected String label;
    protected String messageLabel = null;

    /**
     *
     */
    public CustomKPI() {
    }

    @Override
    public void init(KPIConfig kpiConfig) {

        if (kpiConfig != null) {
            if (kpiConfig.label != null) {
                label = kpiConfig.label;
            }
            this.setEnabled(kpiConfig.enabled);
            messageLabel = kpiConfig.messageLabel;
            ranges = asRanges(kpiConfig.label, kpiConfig.ranges);
            addSpecsFromConfig(kpiConfig.matches);
        }

        this.valueFunction = component -> {
            if (component instanceof Labeled) {
                return ((Labeled) component).getLabel(label);
            }
            throw new RuntimeException("Custom KPIs can only evaluate labels (custom fields).");
        };
        this.msgFunction = component -> {
            if (component instanceof Labeled) {
                return ((Labeled) component).getLabel(messageLabel);
            }
            return null;
        };
    }

    @Override
    protected List<StatusValue> getStatusValues(@Nullable String value, @Nullable String message) {

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
            boolean anyMatch = false;
            if (value != null) {
                anyMatch = matches.get(status).stream().anyMatch(stringBooleanFunction -> stringBooleanFunction.apply(value));
            }
            if (anyMatch) {
                values.add(new StatusValue(label, status, message));
                break;
            }
        }

        return values;
    }

    protected Map<Status, Range<Double>> asRanges(String label, Map<String, String> ranges) {
        Map<Status, Range<Double>> rangeMap = new HashMap<>();
        if (ranges == null) {
            return rangeMap;
        }

        ranges.forEach((statusString, s) -> {
            String[] split;
            if (s.contains(SEPARATOR)) {
                split = s.split(SEPARATOR);
            } else {
                split = new String[2];
                split[0] = s;
                split[1] = s;
            }
            try {
                rangeMap.put(Status.from(statusString), Range.between(Double.valueOf(split[0]), Double.valueOf(split[1])));
            } catch (NumberFormatException e) {
                throw new ProcessingException(String.format("Failed to parse KPI '%s' range: %s", label, s), e);
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

    protected void addSpecsFromConfig(@Nullable Map<String, String> input) {
        Map<Status, String> statusPatterns = new HashMap<>();
        if (input != null) {
            input.forEach((s, s2) -> statusPatterns.put(Status.from(s), s2));
            addSpecs(statusPatterns);
        }
    }

    protected void addSpecs(@Nullable Map<Status, String> statusPatterns) {
        if (statusPatterns == null)
            return;

        statusPatterns.forEach((status, strings) -> {
            List<Function<String, Boolean>> specs = Arrays.stream(strings.split(SEPARATOR)).map(s -> {
                try {
                    Pattern p = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                    return (Function<String, Boolean>) s1 -> p.matcher(s1).matches();
                } catch (Exception e) {
                    throw new ProcessingException("Failed to initialize KPI " + this.label + " matcher pattern ", e);
                }

            }).collect(Collectors.toList());
            matches.put(status, specs);
        });
    }
}
