package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Labeled;
import org.apache.commons.collections.map.SingletonMap;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class KubernetesKPI implements KPI {
    public static final String IDENTIFIER = "k8s";


    @Override
    @NonNull
    public List<StatusValue> getStatusValues(Component component) {
        if (!(component instanceof Labeled))
            return new ArrayList<>();


        var statusList = new ArrayList<StatusValue>();
        var counter = new AtomicInteger(0);
        ((Labeled) component).getLabels(Label.k8s).forEach((key, value) -> {
            if (!ObjectUtils.isEmpty(value)) {
                var message = key.replaceFirst("k8s.", "");
                if (message.startsWith("boolcondition.")) {
                    message = message.replaceFirst("boolcondition.", "");
                    statusList.add(new StatusValue(IDENTIFIER + ":" + counter.getAndIncrement(), Status.from(boolCondition(value)), message));
                } else if (message.startsWith("replicacondition.")) {
                    var splitValue = value.split(";");
                    var replicaCondition = replicaCondition(Integer.parseInt(splitValue[0]), Integer.parseInt(splitValue[1]));
                    statusList.add(new StatusValue(IDENTIFIER + ":" + counter.getAndIncrement(), Status.from(String.valueOf(replicaCondition.getValue())), String.valueOf(replicaCondition.getKey())));
                } else {
                    statusList.add(new StatusValue(IDENTIFIER + ":" + counter.getAndIncrement(), Status.from(value), message));
                }
            }
        });
        return statusList;

    }

    private String boolCondition(String flag) {
        if (flag.toLowerCase(Locale.ROOT).equals("true")) {
            return de.bonndan.nivio.assessment.Status.GREEN.toString();
        } else {
            return de.bonndan.nivio.assessment.Status.RED.toString();
        }
    }

    private SingletonMap replicaCondition(Integer replicaCount, Integer replicaCountDesired) {
        var message = String.format("%s of %s Pods are ready", replicaCount, replicaCountDesired);
        if (replicaCount == null) {
            return new SingletonMap("ReadyReplicas count was null", de.bonndan.nivio.assessment.Status.ORANGE.toString());
        } else if (replicaCountDesired == null) {
            return new SingletonMap("Replicas count was null", de.bonndan.nivio.assessment.Status.ORANGE.toString());
        } else if (Objects.equals(replicaCount, replicaCountDesired)) {
            return new SingletonMap("all pods are ready", de.bonndan.nivio.assessment.Status.GREEN.toString());
        } else if (replicaCount == 0) {
            return new SingletonMap(message, de.bonndan.nivio.assessment.Status.RED.toString());
        } else
            return new SingletonMap(message, de.bonndan.nivio.assessment.Status.YELLOW.toString());
    }

    @Override
    public String getDescription() {
        return "Evaluates Kubernetes conditions";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<Status, RangeApiModel> getRanges() {
        return null;
    }

    @Override
    public Map<Status, List<String>> getMatches() {
        return null;
    }
}
