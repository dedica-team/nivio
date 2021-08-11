package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Labeled;
import org.apache.commons.collections.map.SingletonMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class KubernetesKPI implements KPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesKPI.class);
    public static final String IDENTIFIER = "k8s";

    private boolean enabled = true;

    @Override
    @NonNull
    public List<StatusValue> getStatusValues(Assessable assessable) {
        if (!(assessable instanceof Labeled))
            return new ArrayList<>();


        var statusList = new ArrayList<StatusValue>();
        var counter = new AtomicInteger(0);
        ((Labeled) assessable).getLabels(Label.k8s).forEach((key, value) -> {
            if (ObjectUtils.isEmpty(value)) {
                return;
            }
            StatusValue statusValue;
            var message = key.replaceFirst("k8s.", "");
            if (message.startsWith("boolcondition.")) {
                message = message.replaceFirst("boolcondition.", "");
                statusValue = new StatusValue(IDENTIFIER + ":" + counter.getAndIncrement(), Status.from(boolCondition(value)), message);
            } else if (message.startsWith("replicacondition.")) {
                var splitValue = value.split(";");
                var replicaCount = getIntegerValue(splitValue, 0);
                var replicaCountDesired = getIntegerValue(splitValue, 1);
                var replicaCondition = replicaCondition(replicaCount, replicaCountDesired);
                statusValue = new StatusValue(IDENTIFIER + ":" + counter.getAndIncrement(), Status.from(String.valueOf(replicaCondition.getValue())), String.valueOf(replicaCondition.getKey()));
            } else {
                statusValue = new StatusValue(IDENTIFIER + ":" + counter.getAndIncrement(), Status.from(value), message);
            }
            statusList.add(statusValue);
        });
        return statusList;
    }

    private Integer getIntegerValue(String[] splitValue, int position) {
        try {
            return Integer.parseInt(splitValue[position]);
        } catch (NumberFormatException e) {
            LOGGER.warn(e.getMessage());
        }
        return null;
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
        }
        if (replicaCountDesired == null) {
            return new SingletonMap("Replicas count was null", de.bonndan.nivio.assessment.Status.ORANGE.toString());
        }
        if (Objects.equals(replicaCount, replicaCountDesired)) {
            return new SingletonMap("all pods are ready", de.bonndan.nivio.assessment.Status.GREEN.toString());
        }
        if (replicaCount == 0) {
            return new SingletonMap(message, de.bonndan.nivio.assessment.Status.RED.toString());
        }
        return new SingletonMap(message, de.bonndan.nivio.assessment.Status.YELLOW.toString());
    }

    @Override
    public String getDescription() {
        return "Evaluates Kubernetes conditions";
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
        return null;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
