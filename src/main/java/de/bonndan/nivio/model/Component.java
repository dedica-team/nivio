package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;

/**
 * Base for graph objects and dtos.
 *
 * Is not {@link de.bonndan.nivio.assessment.Assessable}
 */
public interface Component extends Linked, Labeled {

    /**
     * Returns the local level identifier
     */
    @NonNull
    String getIdentifier();

    /**
     * @return the fqi to identify the landscape item
     */
    @NonNull
    URI getFullyQualifiedIdentifier();

    /**
     * @return the identifier of the parent
     */
    String getParentIdentifier();

    /**
     * A human readable and/or well known name.
     *
     * Can be null in DTOs.
     */
    @Nullable
    String getName();

    /**
     * @return a string describing the component
     */
    @Nullable
    String getDescription();

    /**
     * @return a string describing the owner
     */
    @Nullable
    String getOwner();

    /**
     * @return a string describing the type
     */
    @Nullable
    String getType();

    /**
     * @return an icon url
     */
    @Nullable
    String getIcon();

    /**
     * @return a html color
     */
    @Nullable
    String getColor();

}
