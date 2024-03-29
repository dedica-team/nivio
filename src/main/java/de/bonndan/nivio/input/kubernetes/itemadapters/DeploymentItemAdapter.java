package de.bonndan.nivio.input.kubernetes.itemadapters;


import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.apps.Deployment;

import java.util.List;
import java.util.Map;

public class DeploymentItemAdapter implements ItemAdapter {
    private final Deployment deployment;

    public DeploymentItemAdapter(Deployment deployment) {
        this.deployment = deployment;
    }

    @Override
    public Map<String, String> getLabels() {
        return deployment.getMetadata().getLabels();
    }

    @Override
    public List<OwnerReference> getOwnerReferences() {
        return deployment.getMetadata().getOwnerReferences();
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


}
