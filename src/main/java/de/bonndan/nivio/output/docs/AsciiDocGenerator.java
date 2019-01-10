package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.landscape.Groups;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.ServiceItem;
import de.bonndan.nivio.landscape.ServiceItems;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import static org.springframework.util.StringUtils.isEmpty;


public class AsciiDocGenerator {

    private static final String NL = "\n";

    public String toDocument(Landscape landscape) {

        return writeLandscape(landscape);
    }

    private String writeLandscape(Landscape landscape) {
        final StringBuilder builder = new StringBuilder();

        builder.append("= " + landscape.getName() + NL);
        builder.append(landscape.getContact() + NL);
        builder.append(new Date().toString() + NL);
        builder.append(":title: " + landscape.getName() + NL + NL);

        builder.append("[horizontal]" + NL);
        builder.append("Identifier:: " + landscape.getIdentifier() + NL);
        builder.append("Contact:: " + landscape.getContact() + NL);

        builder.append(writeGroups(ServiceItems.getGroups(landscape)));
        return builder.toString();
    }

    private String writeGroups(Groups groups) {
        final StringBuilder builder = new StringBuilder();
        groups.getAll().forEach((s, landscapeItems) -> {
            builder.append(NL + "== Group: " + s + NL);
            landscapeItems.forEach(item -> builder.append(writeItem(item)));
        });

        return builder.toString();
    }

    private String writeItem(ServiceItem item) {
        final StringBuilder builder = new StringBuilder();

        builder.append(NL + "=== " + (isEmpty(item.getName()) ? item.getIdentifier() : item.getName()) + NL);
        builder.append(nice(item.getDescription()) + NL);
        if (!isEmpty(item.getNote())) {
            builder.append(item.getNote() + NL);
        }

        builder.append("[horizontal]" + NL);
        builder.append("FQI:: " + item.getFullyQualifiedIdentifier() + NL);
        builder.append("Name:: " + nice(item.getName()) + NL);
        builder.append("Short Name:: " + nice(item.getShort_name()) + NL);
        builder.append("Type:: " + item.getType() + NL);
        builder.append("Homepage:: " + nice(item.getHomepage()) + NL);
        builder.append("Repository:: " + nice(item.getRepository()) + NL);
        builder.append("Tags:: " + nice(item.getTags()) + NL);
        builder.append("Contact:: " + nice(item.getContact()) + NL);
        builder.append("Team:: " + nice(item.getTeam()) + NL);
        builder.append("Owner:: " + nice(item.getOwner()) + NL);
        builder.append("Software:: " + nice(item.getSoftware()) + NL);
        builder.append("Version:: " + nice(item.getVersion()) + NL);
        builder.append("Machine:: " + nice(item.getMachine()) + NL);
        builder.append("Scale:: " + nice(item.getScale()) + NL);
        builder.append("Visibility:: " + nice(item.getVisibility()) + NL);
        builder.append("Networks:: " + nice(item.getNetworks()) + NL);

        item.getStatuses().forEach(statusItem -> {
            builder.append(nice(statusItem.getLabel()) + ":: [" + statusItem.getStatus() + "]*" + statusItem.getStatus() + "* " + nice(statusItem.getMessage()) + NL);
        });

        builder.append(NL);

        if (item.getDataFlow() != null && item.getDataFlow().size() > 0) {
            builder.append(".Data flow" + NL);
            item.getDataFlow().forEach(df -> {
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
