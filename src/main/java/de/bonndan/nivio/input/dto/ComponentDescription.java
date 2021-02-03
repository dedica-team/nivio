package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Linked;
import org.springframework.lang.Nullable;

/**
 * Base interface for input DTOs.
 *
 *
 */
public interface ComponentDescription extends Labeled, Linked {

    /**
     * @return the identifier (not globally unique, but within its context)
     */
    String getIdentifier();

    /**
     * @return the name or null if not set
     */
    @Nullable
    String getName();

    void setName(String name);

    /**
     * @return the description if present or null
     */
    @Nullable
    String getDescription();

    void setDescription(String description);

    /**
     * @return the owner or null
     */
    @Nullable
    String getOwner();

    void setOwner(String owner);

    /**
     * A email address or other means to contact a responsible person.
     *
     * @return the contact or null
     */
    @Nullable
    String getContact();

    void setContact(String contact);
}
