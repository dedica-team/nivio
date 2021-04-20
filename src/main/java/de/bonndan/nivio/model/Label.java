package de.bonndan.nivio.model;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Landscape component labels (to be used like fields).
 *
 * All names are used in lowercase variant.
 *
 */
public enum Label {

    capability("The capability the service provides for the business or, in case of infrastructure," +
            " the technical capability like enabling service discovery, configuration, secrets, or persistence."),

    color("A hex color code to override the inherited group color"),

    costs("Running costs of the item."),

    fill("Background image (for displaying purposes)."),
    icon("Icon/image (for displaying purposes)."),

    health("Description of the item's health status."),

    layer("A technical layer."),

    lifecycle("A lifecycle phase (``PLANNED|plan``, ``INTEGRATION|int``, ``PRODUCTION|prod``, ``END_OF_LIFE|eol|end``)."),

    note("A custom note."),

    scale("Number of instances."),

    security("Description of the item's security status."),

    shortname("Abbreviated name."),

    software("Software/OS name."),

    stability("Description of the item's stability."),

    team("Name of the responsible team (e.g. technical owner)."),

    type("The type (service, database, queue, load balancer, etc.)."),

    version("The version (e.g. software version or protocol version)."),

    visibility("Visibility to other items."),

    network("Prefix for network labels.",true),

    status("Prefix for status labels. Can be used as prefix for all other labels to mark a status for the label.", true),

    condition("Prefix for condition labels.", true);

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

    /**
     * Exports labels with their meanings as map.
     *
     * @param includePrefixes include labels which are prefixes
     * @return key is label, value is meaning
     */
    public static Map<String, String> export(boolean includePrefixes) {
        Map<String, String> labelExport = new LinkedHashMap<>();
        Arrays.stream(Label.values())
                .filter(label -> includePrefixes || !label.isPrefix)
                .forEach(label -> labelExport.put(label.name(), label.meaning));
        return labelExport;
    }
}
