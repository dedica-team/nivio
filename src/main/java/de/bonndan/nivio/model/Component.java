package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Base for landscapes, groups and items.
 */
public interface Component {

    /**
     * Returns the landscape-wide unique identifier of a server or application.
     */
    @NonNull
    String getIdentifier();

    /**
     * @return the fqi to identify the landscape item
     */
    @NonNull
    FullyQualifiedIdentifier getFullyQualifiedIdentifier();

    /**
     * A human readable and/or well known name.
     *
     * Can be null in DTOs.
     */
    @Nullable
    String getName();

    /**
     * A way to contact the responsible person.
     */
    @Nullable
    String getContact();

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
