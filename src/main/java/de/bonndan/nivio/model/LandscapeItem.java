package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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

    String IDENTIFIER_VALIDATION = "^[a-zA-Z0-9\\.\\:_-]{2,256}$";

    /**
     * @return the group name (used as identifier)
     */
    @Nullable String getGroup();

    /**
     * @return a type like "server", "container", "owner" ...
     */
    @Nullable String getType();

    /**
     * @return a set of {@link InterfaceItem}
     */
    @NonNull Set<InterfaceItem> getInterfaces();

    /**
     * @return Relations to other items
     */
    @NonNull Set<? extends RelationItem> getRelations();
}
