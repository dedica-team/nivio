package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.kubernetes.details.DefaultDetails;
import de.bonndan.nivio.input.kubernetes.details.Details;
import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import de.bonndan.nivio.input.kubernetes.status.Status;

public class K8sItemBuilder {
    private final String type;
    private final ItemAdapter itemAdapter;
    private Details details;
    private Status status;

    public K8sItemBuilder(String type, ItemAdapter itemAdapter) {
        this.details = new DefaultDetails();
        this.itemAdapter = itemAdapter;
        this.status = null;
        this.type = type;
    }

    public K8sItemBuilder addStatus(Status status) {
        this.status = status;
        return this;
    }

    public K8sItemBuilder addDetails(Details details) {
        this.details = details;
        return this;
    }

    public K8sItem build() {
        return new K8sItem(type, itemAdapter, status, details);
    }
}
