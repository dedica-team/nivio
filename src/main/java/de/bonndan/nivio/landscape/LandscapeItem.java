package de.bonndan.nivio.landscape;

public interface LandscapeItem {

    String INFRASTRUCTURE = "infrastructure";
    String APPLICATION = "application";

    String IDENTIFIER_VALIDATION = "^[a-z0-9\\.\\:_-]{3,256}$";

    /**
     * Returns the landscape-wide unique identifier of a server or application.
     *
     */
    String getIdentifier();
}
