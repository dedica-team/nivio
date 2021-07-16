package de.bonndan.nivio.input.kubernetes.items;


import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.kubernetes.K8sItem;
import de.bonndan.nivio.input.kubernetes.K8sItemBuilder;
import de.bonndan.nivio.input.kubernetes.details.DefaultDetails;
import de.bonndan.nivio.input.kubernetes.details.DeploymentDetails;
import de.bonndan.nivio.input.kubernetes.status.BoolStatus;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
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

    public static List<K8sItem> getDeploymentItems(@NonNull KubernetesClient client) {
        var deploymentList = client.apps().deployments().list().getItems();
        return deploymentList.stream().map(deployment -> {
            var deploymentItem = new K8sItemBuilder(deployment.getMetadata().getName(), deployment.getMetadata().getUid(), ItemType.DEPLOYMENT, new DeploymentItem(deployment)).addStatus(new BoolStatus()).addDetails(new DeploymentDetails(new DefaultDetails())).build();
            deployment.getStatus().getConditions().forEach(condition -> deploymentItem.addStatus(condition.getType(), condition.getStatus()));
            return deploymentItem;
        }).collect(Collectors.toList());
    }
}
