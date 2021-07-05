package de.bonndan.nivio.assessment.kpi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Labeled;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomKPI.class);

    public static final String SEPARATOR = ";";

    /**
     * For each {@link Status} a numeric range can be defined (includes lower/upper limit).
     */
    protected Map<Status, Range<Double>> ranges;

    /**
     * For each {@link Status} a set of regular expressions can be defined which indicate the status.
     */
    private final Map<Status, List<Function<String, Boolean>>> matchSpecs = new HashMap<>();

    protected String label;

    protected Map<Status, List<String>> matchers;

    /**
     *
     */
    public CustomKPI() {
        //required for deserialization
    }

    @Override
    public void init(KPIConfig kpiConfig) {

        if (kpiConfig != null) {
            if (kpiConfig.label != null) {
                label = kpiConfig.label;
            }
            if (kpiConfig.description != null) {
                description = kpiConfig.description;
            }
            this.setEnabled(kpiConfig.enabled);
            if (!StringUtils.isEmpty(kpiConfig.messageTemplate)) {
                messageTemplate = kpiConfig.messageTemplate;
            }

            ranges = asRanges(kpiConfig.label, kpiConfig.ranges);
            addSpecsFromConfig(kpiConfig.matches);
        }

        this.valueFunction = component -> {
            if (component instanceof Labeled) {
                return ((Labeled) component).getLabel(label);
            }
            LOGGER.error("Custom KPIs can only evaluate labels (custom fields).");
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

            if (!matchSpecs.containsKey(status)) {
                continue;
            }
            var anyMatch = false;
            if (value != null) {
                anyMatch = matchSpecs.get(status).stream().anyMatch(stringBooleanFunction -> stringBooleanFunction.apply(value));
            }
            if (anyMatch) {
                values.add(new StatusValue(label, status, message));
                break;
            }
        }

        return values;
    }

    @Override
    public Map<Status, RangeApiModel> getRanges() {
        if (ranges == null) return null;
        return sorted(
                ranges.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new RangeApiModel(entry.getValue())))
        );
    }

    @Override
    public Map<Status, List<String>> getMatches() {
        return sorted(matchers);
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
        }
        addSpecs(statusPatterns);
    }

    protected void addSpecs(@Nullable Map<Status, String> statusPatterns) {
        if (statusPatterns == null) {
            return;
        }

        Map<Status, List<String>> matchersPerStatus = statusPatterns.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Arrays.asList(e.getValue().split(SEPARATOR))
        ));

        matchersPerStatus.forEach((status, strings) -> {
            List<Function<String, Boolean>> specs = strings.stream().map(s -> {
                try {
                    var p = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                    return (Function<String, Boolean>) s1 -> p.matcher(s1).matches();
                } catch (Exception e) {
                    throw new ProcessingException(String.format("Failed to initialize KPI %s matcher pattern ", this.label), e);
                }

            }).collect(Collectors.toList());
            matchSpecs.put(status, specs);
        });

        this.matchers = matchersPerStatus;
    }
}
