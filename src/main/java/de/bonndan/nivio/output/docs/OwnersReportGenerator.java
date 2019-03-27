package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.landscape.Groups;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.Service;
import de.bonndan.nivio.landscape.ServiceItem;
import de.bonndan.nivio.output.FormatUtils;
import de.bonndan.nivio.output.Icons;
import de.bonndan.nivio.util.Color;
import j2html.tags.ContainerTag;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;
import static org.springframework.util.StringUtils.isEmpty;

public class OwnersReportGenerator extends HtmlGenerator {


    public String toDocument(Landscape landscape) {

        return writeLandscape(landscape);
    }

    private String writeLandscape(Landscape landscape) {

        return html(
                getHead(landscape),
                body(
                        h1("Owner Report: " + landscape.getName()),
                        br(),
                        rawHtml(writeOwnerGroups(Groups.by(ServiceItem::getOwner, new ArrayList<>(landscape.getServices()))))
                )
        ).renderFormatted();
    }

    private String writeOwnerGroups(Groups ownerGroups) {
        final StringBuilder builder = new StringBuilder();
        ownerGroups.getAll().forEach((owner, landscapeItems) -> {
            builder.append(
                    h2(rawHtml(owner)).attr("class", "rounded").render()
            );
            builder.append(writeGroups(Groups.by(ServiceItem::getGroup, landscapeItems)).render());
        });

        return builder.toString();
    }

    private ContainerTag writeGroups(Groups groups) {
        List<ContainerTag> collect = new ArrayList<>();
        groups.getAll().entrySet().forEach(entry -> collect.add(writeGroup(entry)));
        return ul().with(collect);
    }

    private ContainerTag writeGroup(Map.Entry<String, List<ServiceItem>> services) {
        return li().with(services.getValue().stream().map(this::writeItem));
    }

    private ContainerTag writeItem(ServiceItem item) {
        String groupColor = "#" + Color.nameToRGB(item.getGroup());

        return div(rawHtml("<span style=\"color: " + groupColor + "\">&#9899;</span> " + FormatUtils.nice(item.getGroup()) +": " + item.toString() + " (" + item.getFullyQualifiedIdentifier().toString() +  ")"));

    }

}
