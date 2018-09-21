package de.bonndan.nivio.landscape;

public interface LandscapeItem {

    String TYPE_INFRASTRUCTURE = "infrastructure";
    String TYPE_APPLICATION = "application";
    String TYPE_INGRESS = "ingress";

    String IDENTIFIER_VALIDATION = "^[a-z0-9\\.\\:_-]{3,256}$";

    /**
     * Returns the landscape-wide unique identifier of a server or application.
     *
     */
    String getIdentifier();

    /**
     * @return the type (ingress, service, infrastructure)
     */
    String getType();

    String getName();
    String getShort_name();

    String getGroup();

    String getSoftware();

    String getVersion();

    String getHomepage();

    String getRepository();

    String getContact();

    String getTeam();

    String getVisibility();

    String getMachine();

    String getScale();

    String getHost_type();

    String getNetwork();

    String getDescription();

    String[] getTags();

    String getNote();

    Integer getPort();

    String getProtocol();

    String getOwner();
}
