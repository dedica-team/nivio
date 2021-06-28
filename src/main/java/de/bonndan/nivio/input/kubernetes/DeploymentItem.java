package de.bonndan.nivio.input.kubernetes;


import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.Deployment;

public class DeploymentItem extends Item {
    private Deployment deployment;

    public void setDeployment(Deployment deployment) {
        this.deployment = deployment;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    @Override
    public HasMetadata getWrappedItem() {
        return deployment;
    }


}
