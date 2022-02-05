package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Set;

/**
 * Group is a container for {@link Item}s.
 *
 * parent: {@link Context}
 * children: {@link Item}
 */
public class Group extends GraphComponent {

    Group(@NonNull final String identifier,
          @Nullable final String name,
          @Nullable final String owner,
          @Nullable final String contact,
          @Nullable final String description,
          @Nullable final String type,
          @NonNull final Context parent
    ) {
        super(identifier, name, owner, contact, description, type, parent.getFullyQualifiedIdentifier());
    }

    @NonNull
    @Override
    public Context getParent() {
        return _getParent(Context.class);
    }

    @NonNull
    @Override
    public Set<Item> getChildren() {
        return getChildren(component -> true, Item.class);
    }
}
