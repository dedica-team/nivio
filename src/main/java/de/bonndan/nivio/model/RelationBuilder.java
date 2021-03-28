package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Optional;

public class RelationBuilder {

    public static RelationDescription createProviderDescription(ItemDescription source, String target) {
        return createProviderDescription(source.getIdentifier(), target);
    }

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

    public static Relation createProviderRelation(Item source, Item target) {
        return new Relation(source, target, null, null, RelationType.PROVIDER);
    }

    public static RelationDescription provides(ItemDescription source, ItemDescription target) {
        return provides(source.getIdentifier(), target);
    }

    public static RelationDescription provides(String source, ItemDescription target) {
        RelationDescription relationDescription = new RelationDescription();
        relationDescription.setSource(source);
        relationDescription.setTarget(target.getFullyQualifiedIdentifier().toString());
        relationDescription.setType(RelationType.PROVIDER);
        return relationDescription;
    }

    /**
     * Returns a new relation with values updated by the description.
     *
     * @param existing    existing relation
     * @param description incoming data
     * @return new copy
     */
    @NonNull
    public static Relation update(@NonNull final Relation existing, @NonNull final RelationDescription description) {
        Objects.requireNonNull(existing);
        Objects.requireNonNull(description);
        return new Relation(existing.getSource(), existing.getTarget(), description.getDescription(), description.getFormat(), existing.getType());
    }
}
