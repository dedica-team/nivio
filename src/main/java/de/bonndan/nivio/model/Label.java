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

    capability("The capability the service provides for the business, or in case of infrastructure" +
            " the technical capability like enabling service discovery, configuration, secrets or persistence."),

    costs("Running costs of the item."),

    health("description of the item's health status"),

    layer("a technical layer"),

    lifecycle("A lifecycle phase (PLANNED|plan, INTEGRATION|int, PRODUCTION|prod, END_OF_LIFE|eol|end)"),

    note("a custom note"),

    scale("number of instances"),

    security("description of the item's security status"),

    shortname("abbreviated name"),

    software("Software/OS name"),

    stability("description of the item's stability"),

    team("Name of the responsible team (e.g. technical owner)"),

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
