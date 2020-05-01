package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.FormatUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Generates a document using asciidoc, which is cumbersome.
 *
 *
 */
@Deprecated
public class AsciiDocGenerator {

    private static final String NL = "\n";

    public String toDocument(LandscapeImpl landscape) {

        return writeLandscape(landscape);
    }

    private String writeLandscape(LandscapeImpl landscape) {
        final StringBuilder builder = new StringBuilder();

        builder.append("= " + landscape.getName() + NL);
        builder.append(landscape.getContact() + NL);
        builder.append(new Date().toString() + NL);
        builder.append(":title: " + landscape.getName() + NL + NL);

        builder.append("[horizontal]" + NL);
        builder.append("Identifier:: " + landscape.getIdentifier() + NL);
        builder.append("Contact:: " + landscape.getContact() + NL);

        builder.append(writeGroups(Groups.from(landscape)));
        return builder.toString();
    }

    private String writeGroups(Groups groups) {
        final StringBuilder builder = new StringBuilder();
        groups.getAll().forEach((s, landscapeItems) -> {
            builder.append(NL + "== Group: " + s + NL);
            landscapeItems.forEach(item -> builder.append(writeItem((Item) item)));
        });

        return builder.toString();
    }

    private String writeItem(Item item) {
        final StringBuilder builder = new StringBuilder();

        builder.append(NL + "=== " + (isEmpty(item.getName()) ? item.getIdentifier() : item.getName()) + NL);
        builder.append(nice(item.getDescription()) + NL);
        if (!isEmpty(item.getLabel(Label.NOTE))) {
            builder.append(item.getLabel(Label.NOTE) + NL);
        }

        builder.append("[horizontal]" + NL);
        builder.append("FQI:: " + item.getFullyQualifiedIdentifier() + NL);
        builder.append("Name:: " + nice(item.getName()) + NL);
        builder.append("Short Name:: " + nice(item.getLabel(Label.SHORTNAME)) + NL);
        builder.append("Type:: " + item.getType() + NL);
        builder.append("Links:: " + item.getLinks().entrySet().stream()
                .map(stringURLEntry -> stringURLEntry.getValue().toString() + "[" + stringURLEntry.getKey() + "]")
                .collect(Collectors.joining(" ")) + NL);
        builder.append("Tags:: " + FormatUtils.nice(item.getLabels(Tagged.LABEL_PREFIX_TAG)) + NL);
        builder.append("Contact:: " + nice(item.getContact()) + NL);
        builder.append("Team:: " + nice(item.getLabel(Label.TEAM)) + NL);
        builder.append("Owner:: " + nice(item.getOwner()) + NL);
        builder.append("Software:: " + nice(item.getLabel(Label.SOFTWARE)) + NL);
        builder.append("Version:: " + nice(item.getLabel(Label.VERSION)) + NL);
        builder.append("Machine:: " + nice(item.getLabel(Label.MACHINE)) + NL);
        builder.append("Scale:: " + nice(item.getLabel(Label.SCALE)) + NL);
        builder.append("Visibility:: " + nice(item.getLabel(Label.VISIBILITY)) + NL);
        builder.append("Networks:: " + FormatUtils.nice(item.getLabels(Label.PREFIX_NETWORK)) + NL);

        //TODO include assessment

        builder.append(NL);

        if (item.getRelations() != null && item.getRelations().size() > 0) {
            builder.append(".Relations" + NL);
            item.getRelations().forEach(df -> {
                builder.append("* " + df.getTarget() + ": ");
                builder.append(df.getFormat() + " " + df.getDescription());
                builder.append(NL);
            });

        }
        builder.append(NL);

        if (item.getInterfaces() != null && item.getInterfaces().size() > 0) {
            builder.append(".Interfaces" + NL);
            item.getInterfaces().forEach(interfaceItem -> {
                builder.append("* ");
                if (!StringUtils.isEmpty(interfaceItem.getDescription()))
                    builder.append(interfaceItem.getDescription());

                if (!StringUtils.isEmpty(interfaceItem.getFormat()))
                    builder.append(", format: " + interfaceItem.getFormat());
                if (interfaceItem.getUrl() != null && !StringUtils.isEmpty(interfaceItem.getUrl().toString()))
                    builder.append(", " + interfaceItem.getUrl());
                builder.append(NL);
            });
        }
        builder.append(NL);


        return builder.toString();
    }

    private String nice(Collection<String> strings) {
        if (strings == null)
            return "-";
        return nice(strings.toArray(new String[]{}));
    }

    private String nice(String[] tags) {
        if (tags == null || tags.length == 0)
            return "-";

        return StringUtils.arrayToCommaDelimitedString(tags);
    }

    private String nice(String string) {
        if (isEmpty(string))
            return "-";

        string = string.replace("_", " ");
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
