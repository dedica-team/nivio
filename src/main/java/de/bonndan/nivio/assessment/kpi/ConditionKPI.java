package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.input.kubernetes.InputFormatHandlerKubernetes;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.output.dto.RangeApiModel;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Evaluates the "condition.*" labels, see {@link InputFormatHandlerKubernetes}
 *
 * this is similar to the {@link HealthKPI}, it only works on a different field.
 */
public class ConditionKPI implements KPI {

    public static final String IDENTIFIER = "condition";

    private static final Map<Status, List<String>> matches = new LinkedHashMap<>();

    static {
        matches.put(Status.RED, List.of("One condition is not met."));
        matches.put(Status.GREEN, List.of("All conditions are met."));
    }

    private boolean enabled = true;

    @Override
    @NonNull
    public List<StatusValue> getStatusValues(Assessable component) {
        if (!(component instanceof Labeled)) {
            return new ArrayList<>();
        }

        var status = Status.UNKNOWN;
        var message = "";
        for (Map.Entry<String, String> entry : ((Labeled) component).getLabels(Label._condition).entrySet()) {
            String key = entry.getKey();
            String flag = entry.getValue();
            if (!StringUtils.hasLength(flag))
                continue;
            if (flag.equalsIgnoreCase("false")) {
                status = Status.RED;
                message = key;
            }

            if (flag.equalsIgnoreCase("true") && !status.equals(Status.RED)) {
                status = Status.GREEN;
                message = key;
            }
        }

        if (Status.UNKNOWN.equals(status)) {
            return new ArrayList<>();
        }
        return Collections.singletonList(new StatusValue(component.getAssessmentIdentifier(), IDENTIFIER, status, message));
    }

    @Override
    public String getDescription() {
        return "Evaluates condition labels.";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Map<Status, RangeApiModel> getRanges() {
        return null;
    }

    @Override
    public Map<Status, List<String>> getMatches() {
        return sorted(matches);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
