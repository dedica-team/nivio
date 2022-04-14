package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * parent: {@link Item}
 * children: none
 */
public class Part extends GraphComponent {

    public Part(@NonNull final String identifier,
                @Nullable final String name,
                @Nullable final String owner,
                @Nullable final String contact,
                @Nullable final String description,
                @Nullable final String type,
                @NonNull final Item parent
    ) {
        super(identifier, name, owner, contact, description, type, Objects.requireNonNull(parent).getFullyQualifiedIdentifier());
    }

    @NonNull
    @Override
    public Item getParent() {
        return _getParent(Item.class);
    }

    @NonNull
    @Override
    public Set<? extends GraphComponent> getChildren() {
        return Collections.emptySet();
    }
}
