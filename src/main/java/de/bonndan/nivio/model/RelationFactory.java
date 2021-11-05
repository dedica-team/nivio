package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Factory to create {@link Relation} instances.
 *
 *
 */
public class RelationFactory {

    public static RelationDescription createProviderDescription(ItemDescription source, String target) {
        return createProviderDescription(source.getIdentifier(), target);
    }

    private RelationFactory() {}

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
    public static RelationDescription createDataflowDescription(ItemDescription source, String target) {
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
     * @param landscape   the landscape containing source and target items
     * @return new copy
     */
    @NonNull
    public static Relation update(@NonNull final Relation existing,
                                  @NonNull final RelationDescription description,
                                  @NonNull final Landscape landscape
    ) {
        Objects.requireNonNull(existing);
        Objects.requireNonNull(description);
        Objects.requireNonNull(landscape);

        Relation relation = new Relation(
                landscape.findOneBy(description.getSource(), existing.getSource().getGroup()),
                landscape.findOneBy(description.getTarget(), existing.getTarget().getGroup()),
                description.getDescription(),
                description.getFormat(),
                description.getType()
        );

        Labeled.merge(description, relation);
        return relation;
    }

    /**
     * Create a new relation object
     *
     * @param origin              the item the description relates to
     * @param relationDescription the input dto
     * @param landscape           the landscape to pick ends from
     * @return a new relation object
     */
    @NonNull
    public static Relation create(@NonNull final Item origin,
                                  @NonNull final RelationDescription relationDescription,
                                  @NonNull final Landscape landscape
    ) {
        Objects.requireNonNull(relationDescription);
        Objects.requireNonNull(landscape);

        Relation relation = new Relation(
                landscape.findOneBy(relationDescription.getSource(), origin.getGroup()),
                landscape.findOneBy(relationDescription.getTarget(), origin.getGroup()),
                relationDescription.getDescription(),
                relationDescription.getFormat(),
                relationDescription.getType()
        );

        Labeled.merge(relationDescription, relation);
        return relation;
    }

    /**
     * Creates a relation instance without any checks.
     */
    public static Relation createForTesting(Item source, Item target) {
        return new Relation(source, target, null, null, null);
    }
}
