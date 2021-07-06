package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Labeled;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KubernetesKPI implements KPI {
    public static final String IDENTIFIER = "k8s";


    @Override
    public List<StatusValue> getStatusValues(Component component) {
        if (!(component instanceof Labeled))
            return new ArrayList<>();

        var status = Status.UNKNOWN;
        var statusList = new ArrayList<StatusValue>();
        var id = 1;
        for (Map.Entry<String, String> entry : ((Labeled) component).getLabels(Label.condition).entrySet()) {
            String key = entry.getKey();
            String flag = entry.getValue();
            if (StringUtils.isEmpty(flag))
                continue;
            if (key.startsWith("condition.")) {
                var message = key.replaceFirst("condition.", "");
                statusList.add(new StatusValue(IDENTIFIER + ":" + id++, Status.from(flag), message));
            }
        }
        return statusList;

    }

    @Override
    public String getDescription() {
        return "Evaluates Kubernetes conditions";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
