package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.input.kubernetes.InputFormatHandlerKubernetes;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Labeled;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Evaluates the "condition.*" labels, see {@link InputFormatHandlerKubernetes}
 *
 * TODO this is similiar to the {@link HealthKPI}, it only works on a different field.
 */
public class ConditionKPI implements KPI {

    public static final String IDENTIFIER = "condition";

    private boolean enabled = true;

    @Override
    public List<StatusValue> getStatusValues(Component component) {
        if (!(component instanceof Labeled))
            return new ArrayList<>();

        var status = Status.UNKNOWN;
        var message = "";
        for (Map.Entry<String, String> entry : ((Labeled) component).getLabels(Label.condition).entrySet()) {
            String key = entry.getKey();
            String flag = entry.getValue();
            if (StringUtils.isEmpty(flag))
                continue;
            if (flag.toLowerCase().equals("false")) {
                status = Status.RED;
                message = key;
            }

            if (flag.toLowerCase().equals("true") && !status.equals(Status.RED)) {
                status = Status.GREEN;
                message = key;
            }
        }

        if (Status.UNKNOWN.equals(status)) {
            return new ArrayList<>();
        }
        return Collections.singletonList(new StatusValue(IDENTIFIER, status, message));
    }

    @Override
    public String getDescription() {
        return "Evaluates condition labels.";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
