package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Landscape component labels (to be used like fields).
 *
 * All names are used in lowercase variant.
 */
public enum Label {

    capability("The capability the service provides for the business or, in case of infrastructure," +
            " the technical capability like enabling service discovery, configuration, secrets, or persistence."),

    color("A hex color code (items inherit group colors as default)"),

    costs("Running costs of the item."),

    fill("Background image (for displaying purposes)."),
    _filldata("Background image resolved to a data-url."),

    framework("A map of used frameworks (key is name, value is version).", true),
    frameworks("A comma-separated list of frameworks as key-value pairs (key is name, value is version)."),

    icon("Icon/image (for displaying purposes)."),
    _icondata("Icon/image resolved to a data-url"),

    health("Description of the item's health status."),

    label("A custom label (like a note, but very short)."),

    lifecycle("A lifecycle phase (``PLANNED|plan``, ``INTEGRATION|int``, ``PRODUCTION|prod``, ``END_OF_LIFE|eol|end``)."),

    note("A custom note."),

    scale("Number of instances."),

    security("Description of the item's security status."),

    shortname("Abbreviated name."),

    software("Software/OS name."),

    stability("Description of the item's stability."),

    team("Name of the responsible team (e.g. technical owner)."),

    version("The version (e.g. software version or protocol version)."),

    visibility("Visibility to other items."),

    network("Prefix for network labels.", true),

    status("Prefix for status labels. Can be used as prefix for all other labels to mark a status for the label.", true),

    _condition("Prefix for condition labels.", true),

    weight("Importance or relations. Used as factor for drawn width if numbers between 0 and 5 are given.")

   ;

    /**
     * Separator for label key parts.
     * Should not be used outside this package. Use key() methods instead.
     */
    public static final String DELIMITER = ".";

    /**
     * Internal labels start with an underscore. They are not diffed and not part of the public api.
     */
    public static final String INTERNAL_LABEL_PREFIX = "_";

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
     *
     * @param subKey sub-key after the label name prefix
     * @return label key
     */
    public String withPrefix(@NonNull final String subKey) {
        return withPrefix(name(), subKey);
    }

    /**
     * Builds a properly delimited label with custom prefix.
     *
     * @param prefix custom prefix
     * @param subKey sub-key after the label name prefix
     * @return label key
     */
    public static String withPrefix(@NonNull final String prefix, @NonNull final String subKey) {
        return String.format("%s%s%s", prefix, DELIMITER, Objects.requireNonNull(subKey, "Label sub key is null").toLowerCase());
    }

    public static String withPrefix(Label prefix, String key, String suffix) {
        return prefix + DELIMITER + key.toLowerCase() + DELIMITER + suffix;
    }

    /**
     * Exports labels with their meanings as map.
     *
     * @param includePrefixes include labels which are prefixes
     * @return key is label, value is meaning
     */
    public static Map<String, String> export(boolean includePrefixes) {
        Map<String, String> labelExport = new LinkedHashMap<>();
        List<Label> sortedLabels = Arrays.stream(Label.values()).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
        sortedLabels.stream()
                .filter(label -> includePrefixes || !label.isPrefix)
                .filter(label -> !label.name().startsWith(INTERNAL_LABEL_PREFIX))
                .forEach(label -> labelExport.put(label.name(), label.meaning));

        return labelExport;
    }

    public String unprefixed(String key) {
        return key.replace(name() + DELIMITER, "");
    }
}
