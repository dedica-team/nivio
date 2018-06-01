package de.bonndan.nivio.landscape;

public interface LandscapeItem {

    public static final String INFRASTRUCTURE = "infrastructure";
    public static final String APPLICATION = "application";

    public static final String IDENTIFIER_VALIDATION = "^[a-z0-9\\.\\:_-]{3,256}$";

    /**
     * Returns the landscape-wide unique identifier of a server or application.
     *
     */
    String getIdentifier();
}
