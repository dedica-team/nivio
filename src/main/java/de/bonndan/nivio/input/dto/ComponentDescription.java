package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Linked;

/**
 * Base interface for input DTOs, which are mutable objects.
 *
 *
 */
public interface ComponentDescription extends Component, Labeled, Linked {

    void setName(String name);

    void setDescription(String description);

    void setOwner(String owner);

    void setContact(String contact);

    void setIcon(String icon);
}
