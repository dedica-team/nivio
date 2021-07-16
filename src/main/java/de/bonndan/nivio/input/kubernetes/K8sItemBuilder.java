package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.kubernetes.details.DefaultDetails;
import de.bonndan.nivio.input.kubernetes.details.Details;
import de.bonndan.nivio.input.kubernetes.items.Item;
import de.bonndan.nivio.input.kubernetes.status.DefaultStatus;
import de.bonndan.nivio.input.kubernetes.status.Status;

public class K8sItemBuilder {
    private final String name;
    private final String type;
    private final String uid;
    private final Item item;
    private Details details;
    private Status status;

    public K8sItemBuilder(String name, String uid, String type, Item item) {
        this.details = new DefaultDetails();
        this.status = new DefaultStatus();
        this.item = item;
        this.name = name;
        this.type = type;
        this.uid = uid;
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
        return new K8sItem(name, uid, type, item, status, details);
    }
}
