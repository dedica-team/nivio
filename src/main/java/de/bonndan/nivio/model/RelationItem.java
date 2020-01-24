package de.bonndan.nivio.model;

/**
 * A relation between two landscape items.
 *
 * @param <T> Strings for relation descriptions, LandscapeItems in the final graph
 */
public interface RelationItem<T> {

    /**
     * provider (hard dependency) or dataflow (soft relation).
     */
    RelationType getType();

    String getDescription();

    String getFormat();

    /**
     * @return the start item of the relation (e.g. a provider)
     */
    T getSource();

    /**
     * @return the target item
     */
    T getTarget();

}
