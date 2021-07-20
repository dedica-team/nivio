package de.bonndan.nivio.input.kubernetes.itemadapters;


import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import org.springframework.lang.NonNull;

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


}
