package de.bonndan.nivio.model;

/**
 * Landscape component labels (to be used like fields).
 *
 * All names are used in lowercase variant.
 *
 */
public enum Label {

    capability("The capability the service provides for the business, or in case of infrastructure" +
            " the technical capability like enabling service discovery, configuration, secrets or persistence."),

    costs("Running costs of the item."),

    health("description of the item's health status"),

    icon("Name of the icon to render."),

    layer("a technical layer"),

    note("a custom note"),

    scale("number of instances"),

    security("description of the item's security status"),

    SHORTNAME("abbreviated name"),

    software("Software/OS name"),

    stability("description of the item's stability"),

    team("Name of the responsible team"),

    type("the type (service, database, queue, loadbalancer...)"),

    version("The version (e.g. software version, protocol version)"),

    visibility("visibility to other items"),

    network("prefix for network labels",true),

    status("prefix for status labels, can be used as prefix all other labels to mark a status for the label", true),

    condition("prefix for condition labels", true);

    /**
     * Separator for label key parts.
     * Should not be used outside this package. Use key() methods instead.
     */
    static final String DELIMITER = ".";

    public final String meaning;
    public final boolean isPrefix;

    Label(String meaning) {
        this.meaning = meaning;
        this.isPrefix = false;
    }

    Label(String meaning, boolean isPrefix) {
        this.meaning = meaning;
        this.isPrefix = isPrefix;
    }

    /**
     * Builds a properly delimited label key.
     */
    public static String key(Label prefix, String key) {
        return prefix + DELIMITER + key.toLowerCase();
    }

    public static String key(String prefix, String key) {
        return prefix + DELIMITER + key.toLowerCase();
    }

    public static String key(Label prefix, Label key, String suffix) {
        return prefix + DELIMITER + key.toString().toLowerCase() + DELIMITER + suffix;
    }

    public static String key(Label prefix, String key, String suffix) {
        return prefix + DELIMITER + key.toLowerCase() + DELIMITER + suffix;
    }

    public static String key(String prefix, Label key, String suffix) {
        return prefix + DELIMITER + key.toString().toLowerCase() + DELIMITER + suffix;
    }
}
