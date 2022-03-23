package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Factory to create {@link Relation} instances.
 */
public class RelationFactory {

    public static RelationDescription createProviderDescription(ItemDescription source, String target) {
        return createProviderDescription(source.getFullyQualifiedIdentifier().toString(), target);
    }

    private RelationFactory() {
    }

    /**
     * Factory method to create a provider type relation.
     *
     * @param source the provider
     * @param target the consumer
     * @return a new relation description
     */
    public static RelationDescription createProviderDescription(String source, String target) {
        RelationDescription relation = new RelationDescription();
        relation.setType(RelationType.PROVIDER);
        relation.setSource(source);
        relation.setTarget(target);

        return relation;
    }

    /**
     * Creates a new relation description of type dataflow and adds it to the source.
     */
    public static RelationDescription createDataflowDescription(Component source, String target) {
        RelationDescription relationDescription = new RelationDescription();
        relationDescription.setSource(source.getIdentifier());
        relationDescription.setTarget(target);
        relationDescription.setType(RelationType.DATAFLOW);
        return relationDescription;
    }

    /**
     * Factory method to create a new provider type relation.
     *
     * @param source the provider item
     * @param target the consuming item
     * @return a new relation
     */
    public static Relation createProviderRelation(Item source, Item target) {
        return new Relation(source, target, null, null, RelationType.PROVIDER);
    }


    /**
     * Returns a new relation with values updated by the description.
     *
     * @param existing    existing relation
     * @param description incoming data
     * @return new copy
     */
    @NonNull
    public static Relation update(@NonNull final Relation existing,
                                  @NonNull final RelationDescription description,
                                  @NonNull final Item origin,
                                  @NonNull final Item target
    ) {
        if (!Objects.requireNonNull(origin).isAttached()) {
            throw new IllegalArgumentException("Origin must be a non-null graph attached item");
        }

        if (!Objects.requireNonNull(target).isAttached()) {
            throw new IllegalArgumentException("Target must be a non-null graph attached item");
        }

        Objects.requireNonNull(existing);
        Objects.requireNonNull(description);

        Relation relation = new Relation(
                origin,
                target,
                description.getDescription(),
                description.getFormat(),
                description.getType()
        );

        Labeled.merge(description, relation);
        existing.getProcesses().forEach(relation::assignProcess);
        return relation;
    }

    /**
     * Create a new relation object
     *
     * @param origin      the item the description relates to
     * @param target      the item the description targets at
     * @param description the input dto
     * @return a new relation object
     */
    @NonNull
    public static Relation create(@NonNull final Item origin,
                                  @NonNull final Item target,
                                  @NonNull final RelationDescription description
    ) {
        Objects.requireNonNull(description);

        Relation relation = new Relation(
                Objects.requireNonNull(origin),
                Objects.requireNonNull(target),
                description.getDescription(),
                description.getFormat(),
                description.getType()
        );

        Labeled.merge(description, relation);
        return relation;
    }

    /**
     * Creates a relation instance without any checks.
     */
    public static Relation create(@NonNull final Item source, @NonNull final Item target) {
        return new Relation(source, target, null, null, null);
    }

    public static Relation createChild(@NonNull final GraphComponent parent, @NonNull final GraphComponent child) {
        if (!Objects.requireNonNull(parent, "Parent is null").isAttached()) {
            throw new IllegalArgumentException(String.format("Parent %s is not attached to graph.", parent));
        }

        if (!Objects.requireNonNull(child, "Child is null").isAttached()) {
            throw new IllegalArgumentException(String.format("Child %s is not attached to graph.", child));
        }

        var cls = child.getParent().getClass();
        if (!cls.equals(parent.getClass())) {
            throw new IllegalArgumentException(
                    String.format("Child %s requires parent of class %s, but got %s",
                            child.getClass().getSimpleName(),
                            cls.getSimpleName(),
                            parent.getClass().getSimpleName()
                    )
            );
        }
        return new Relation(parent, child, null, null, RelationType.CHILD);
    }
}
