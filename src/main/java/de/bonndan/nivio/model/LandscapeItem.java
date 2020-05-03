package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.StatusValue;

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

    String getGroup();

    String getType();

    Lifecycle getLifecycle();

    Set<InterfaceItem> getInterfaces();

    String getOwner();

    Set<? extends RelationItem> getRelations();
}
