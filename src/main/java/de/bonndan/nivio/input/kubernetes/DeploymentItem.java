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

public class DeploymentItem extends Item {
    private final Deployment deployment;

    public DeploymentItem(String name, String uid, String type, Deployment deployment, LevelDecorator levelDecorator) {
        super(name, uid, type, levelDecorator);
        this.deployment = deployment;
    }

    @Override
    @NonNull
    public HasMetadata getWrappedItem() {
        return deployment;
    }

    @Override
    @NonNull
    public Map<String, String> getStatus() {
        return super.getStatus().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                pair -> {
                    if (pair.getValue().toLowerCase(Locale.ROOT).equals("true")) {
                        return Status.GREEN.toString();
                    } else {
                        return Status.RED.toString();
                    }
                }));
    }

    public static List<Item> getDeploymentItems(@NonNull KubernetesClient client) {
        var deploymentList = client.apps().deployments().list().getItems();
        return deploymentList.stream().map(deployment -> {
            var deploymentItem = new DeploymentItem(deployment.getMetadata().getName(), deployment.getMetadata().getUid(), ItemType.DEPLOYMENT, deployment, new LevelDecorator(4));
            deployment.getStatus().getConditions().forEach(condition -> deploymentItem.addStatus(condition.getType(), condition.getStatus()));
            return deploymentItem;
        }).collect(Collectors.toList());
    }
}
