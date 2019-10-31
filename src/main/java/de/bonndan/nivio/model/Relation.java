package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.Objects;

/**
 * Indication of an incoming or outgoing relation like data flow or dependency (provider).
 *
 * <p>
 * Outgoing flows having a target which matches a service identifier will cause a relation to be created.
 */
public class Relation implements RelationItem<Item>, Serializable {

    @JsonBackReference
    private Item source;

    private Item target;

    private String description;

    private String format;

    private RelationType type;

    Relation() {
    }

    public Relation(Item source, Item target) {
        if (source == null || target == null)
            throw new IllegalArgumentException("Null arguments passed.");

        if (source.equals(target))
            throw new IllegalArgumentException("Relation source and target are equal.");

        this.source = source;
        this.target = target;
    }

    @Override
    public RelationType getType() {
        return type;
    }

    public void setType(RelationType type) {
        this.type = type;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public Item getTarget() {
        return target;
    }

    public void setTarget(Item target) {
        this.target = target;
    }

    @Override
    public Item getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relation relation = (Relation) o;
        return Objects.equals(source, relation.source) && Objects.equals(target, relation.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }

}
