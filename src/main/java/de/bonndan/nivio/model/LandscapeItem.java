package de.bonndan.nivio.model;

import java.util.Set;

/**
 * An item in the landscape (service, machine, owner) having relations to other items (aka graph node).
 *
 *
 */
public interface LandscapeItem extends Component, Labeled, Linked {

    String LAYER_INFRASTRUCTURE = "infrastructure";
    String LAYER_APPLICATION = "applications";
    String LAYER_INGRESS = "ingress";

    String IDENTIFIER_VALIDATION = "^[a-z0-9\\.\\:_-]{3,256}$";

    /**
     * @return the fqi to identify the landscape item
     */
    FullyQualifiedIdentifier getFullyQualifiedIdentifier();

    String getGroup();

    String getType();

    Lifecycle getLifecycle();

    void setStatus(StatusItem statusItem);

    Set<StatusItem> getStatuses();

    Set<InterfaceItem> getInterfaces();

    String getOwner();

    Set<? extends RelationItem> getRelations();
}
