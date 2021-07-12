package de.bonndan.nivio.input.kubernetes;


import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class DeploymentItem implements Item {
    private final Deployment deployment;

    public DeploymentItem(Deployment deployment) {
        this.deployment = deployment;
    }

    @NonNull
    public HasMetadata getWrappedItem() {
        return deployment;
    }

    @NonNull
    public Map<String, String> getStatus(Map<String, String> status) {
        return status.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                pair -> {
                    if (pair.getValue().toLowerCase(Locale.ROOT).equals("true")) {
                        return Status.GREEN.toString();
                    } else {
                        return Status.RED.toString();
                    }
                }));
    }

    public static List<K8sItem> getDeploymentItems(@NonNull KubernetesClient client) {
        var deploymentList = client.apps().deployments().list().getItems();
        return deploymentList.stream().map(deployment -> {
            var deploymentItem = new K8sItem(deployment.getMetadata().getName(), deployment.getMetadata().getUid(), ItemType.DEPLOYMENT, new LevelDecorator(4), new DeploymentItem(deployment));
            deployment.getStatus().getConditions().forEach(condition -> deploymentItem.addStatus(condition.getType(), condition.getStatus()));
            return deploymentItem;
        }).collect(Collectors.toList());
    }
}
