package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.landscape.Groups;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.LandscapeItem;
import de.bonndan.nivio.landscape.LandscapeItems;


public class AsciiDocGenerator {

    private static final String NL = "\n";

    public String toDocument(Landscape landscape) {
        StringBuilder builder = new StringBuilder();
        builder.append("= " + landscape.getName() + NL);
        builder.append("landscape documentation");

        return builder.toString();
    }

    private String writeLandscape(Landscape landscape) {
        final StringBuilder builder = new StringBuilder();
        builder.append("= " + landscape.getName() + NL);
        builder.append("landscape documentation");

        builder.append(writeGroups(LandscapeItems.getGroups(landscape)));
        return builder.toString();
    }

    private String writeGroups(Groups groups) {
        final StringBuilder builder = new StringBuilder();
        groups.getAll().forEach((s, landscapeItems) -> {
            builder.append("== " + s);
            landscapeItems.forEach(item -> builder.append(writeItem(item)));
        });

        return builder.toString();
    }

    private String writeItem(LandscapeItem item) {
        final StringBuilder builder = new StringBuilder();

        builder.append("=== " + item.getName());
        builder.append("FQI: " + item.getFullyQualifiedIdentifier());
        return builder.toString();
    }
}
