package de.bonndan.nivio.input.kubernetes;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;

public class StatefulSetItem extends Item {
    private StatefulSet statefulSet;

    public StatefulSet getStatefulSet() {
        return statefulSet;
    }

    public void setStatefulSet(StatefulSet statefulSet) {
        this.statefulSet = statefulSet;
    }

    @Override
    public HasMetadata getWrappedItem() {
        return statefulSet;
    }
}
