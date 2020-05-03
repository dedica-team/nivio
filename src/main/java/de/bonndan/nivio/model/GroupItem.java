package de.bonndan.nivio.model;

/**
 * A collection of landscape items
 */
public interface GroupItem extends Component, Linked, Labeled {

    String getOwner();

    String getColor();
}
