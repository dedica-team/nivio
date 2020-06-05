package de.bonndan.nivio.model;

/**
 * All names are used in lowercase variant.
 *
 *
 */
public enum Label {

    /**
     * Describes the capability the service provides for the business, or in case of infrastructure the technical
     * capability like enabling service discovery, configuration, secrets or persistence.
     */
    BUSINESS_CAPABILITY,

    /**
     * Running costs of the item.
     *
     */
    COSTS,

    HEALTH,

    HOSTTYPE,

    /**
     * icon to render
     */
    ICON,

    LAYER,

    NOTE,

    SCALE,

    SECURITY,

    SHORTNAME,

    SOFTWARE,

    STABILITY,

    /**
     * Name of the responsible team
     */
    TEAM,

    /**
     * the type (service, database, queue, loadbalancer...)
     */
    TYPE,

    VERSION,

    MACHINE,

    VISIBILITY  ;

    public static final String PREFIX_NETWORK = "network";
    public static final String PREFIX_STATUS = "status";
    public static final String PREFIX_CONDITION = "condition";

    /**
     * Separator for label key parts.
     */
    public static final String DELIMITER = ".";

    /**
     * Builds a properly delimited label key.
     */
    public static String key(String prefix, Label key, String suffix) {
        return prefix + DELIMITER + key.toString().toLowerCase() + DELIMITER + suffix;
    }
}
