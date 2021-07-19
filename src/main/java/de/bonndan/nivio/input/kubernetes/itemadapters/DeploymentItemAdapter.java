package de.bonndan.nivio.input.kubernetes.itemadapters;


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

public class DeploymentItemAdapter implements ItemAdapter {
    private final Deployment deployment;

    public DeploymentItemAdapter(Deployment deployment) {
        this.deployment = deployment;
    }

    @NonNull
    public HasMetadata getWrappedItem() {
        return deployment;
    }

    @Override
    public String getUid() {
        return deployment.getMetadata().getUid();
    }

    @Override
    public String getName() {
        return deployment.getMetadata().getName();
    }

    @Override
    public String getNamespace() {
        return deployment.getMetadata().getNamespace();
    }

    @Override
    public String getCreationTimestamp() {
        return deployment.getMetadata().getCreationTimestamp();
    }

    public String getStrategyType() {
        return deployment.getSpec().getStrategy().getType();
    }

    public static List<K8sItem> getDeploymentItems(@NonNull KubernetesClient client) {
        var deploymentList = client.apps().deployments().list().getItems();
        return deploymentList.stream().map(deployment -> {
            var deploymentItem = new K8sItemBuilder(ItemType.DEPLOYMENT, new DeploymentItemAdapter(deployment)).addStatus(new BoolStatus()).addDetails(new DeploymentDetails(new DefaultDetails())).build();
            deployment.getStatus().getConditions().forEach(condition -> deploymentItem.addStatus(condition.getType(), condition.getStatus()));
            return deploymentItem;
        }).collect(Collectors.toList());
    }
}
