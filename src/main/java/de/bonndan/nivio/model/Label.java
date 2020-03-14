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
    CAPABILITY,

    /**
     * Running costs of the item.
     *
     */
    COSTS,

    HOSTTYPE,

    /**
     * icon to render
     */
    ICON,

    LAYER,

    NOTE,

    SCALE,

    SHORTNAME,

    SOFTWARE,

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
}
