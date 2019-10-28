package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;

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
     *
     */
    public static RelationDescription createDataflowDescription(LandscapeItem source, String target) {
        RelationDescription relationDescription = new RelationDescription();
        relationDescription.setSource(source.getIdentifier());
        relationDescription.setTarget(target);
        relationDescription.setType(RelationType.DATAFLOW);
        return relationDescription;
    }

    public static Relation createProviderRelation(Item source, Item target) {
        Relation relation = new Relation();
        relation.setType(RelationType.PROVIDER);
        relation.setSourceEntity(source);
        relation.setTarget(target.getIdentifier());

        return relation;
    }

    public static RelationDescription copy(RelationItem rel) {
        RelationDescription copy = new RelationDescription();
        copy.setSource(rel.getSource());
        copy.setTarget(rel.getTarget());
        copy.setDescription(rel.getDescription());
        copy.setFormat(rel.getFormat());
        copy.setType(rel.getType());
        return copy;
    }
}
