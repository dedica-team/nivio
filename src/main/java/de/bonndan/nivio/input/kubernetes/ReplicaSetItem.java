package de.bonndan.nivio.input.kubernetes;


import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;

import java.util.List;

public class ReplicaSetItem extends Item {
    ReplicaSet replicaSet;
    List<ServiceItem> serviceItems;


    public ReplicaSet getReplicaSet() {
        return replicaSet;
    }

    public void setReplicaSet(ReplicaSet replicaSet) {
        this.replicaSet = replicaSet;
    }

    public List<ServiceItem> getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(List<ServiceItem> serviceItems) {
        this.serviceItems = serviceItems;
    }

    @Override
    public HasMetadata getWrappedItem() {
        return replicaSet;
    }
}
