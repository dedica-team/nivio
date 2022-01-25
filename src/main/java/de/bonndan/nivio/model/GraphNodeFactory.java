package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ComponentDescription;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;

/**
 * @param <T> produced type
 * @param <D> input dto type
 * @param <P> parent type
 */
public interface GraphNodeFactory<T extends GraphNode<?, ?>, D extends ComponentDescription, P extends GraphNode<?, ?>> {

    /**
     * Merges all absent values from the second param into the first.
     *
     * @param existing copy base
     * @param added    carries additional values
     * @return a new instance, or the first one if the second one is null
     */
    T merge(@NonNull final T existing, @Nullable final T added);

    /**
     * Merges all absent values from the second param into the first.
     *
     * @param base    copy base
     * @param added   group that carries additional values
     * @param builder builder to use
     */
    default void mergeIntoBuilder(@NonNull final T base, final T added, final GraphNodeBuilder<?, ?, ?> builder) {

        Objects.requireNonNull(base, "Base component must not be null");

        builder.withIdentifier(base.getIdentifier());
        builder.withDescription(base.getDescription());
        builder.withOwner(base.getOwner());
        builder.withContact(base.getContact());
        builder.withLinks(base.getLinks());
        builder.withLabels(base.getLabels());

        if (added != null) {
            assignSafe(added.getColor(), s -> builder.getLabels().put(Label.color.name(), s));
            assignSafe(added.getIcon(), s -> builder.getLabels().put(Label.icon.name(), s));
            assignSafe(added.getContact(), builder::withContact);
            assignSafe(added.getDescription(), builder::withDescription);
            assignSafe(added.getOwner(), builder::withOwner);

            added.getLinks().forEach((s, url) -> base.getLinks().putIfAbsent(s, url));
            added.getLabels().forEach((s, val) -> builder.getLabels().putIfAbsent(s, val));
        }
    }

    /**
     * Create a NEW component based on a DTO.
     *
     * @param identifier  identifier
     * @param description dto
     * @param parent      parent of the component
     * @return new instance, linked to parent
     */
    T createFromDescription(@NonNull final String identifier, @NonNull final D description, @NonNull final P parent);

}
