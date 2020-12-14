package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Linked;

/**
 * Base interface for input DTOs.
 *
 *
 */
public interface ComponentDescription extends Labeled, Linked {

    String getIdentifier();

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    String getOwner();

    void setOwner(String owner);
}
