package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.FormatUtils;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.IconService;
import j2html.tags.ContainerTag;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static j2html.TagCreator.*;

public class OwnersReportGenerator extends HtmlGenerator {

    public OwnersReportGenerator(LocalServer localServer, IconService iconService) {
        super(localServer, iconService);
    }

    @Override
    public String toDocument(@NonNull final Landscape landscape, @Nullable final SearchConfig searchConfig) {
        return writeLandscape(landscape, searchConfig != null ? searchConfig.getSearchTerm() : null);
    }

    private String writeLandscape(Landscape landscape, String searchTerm) {

        List<Item> search = searchTerm != null ? new ArrayList<>(landscape.search(searchTerm)) : new ArrayList<>(landscape.getItems().all());
        return html(
                getHead(landscape),
                body(
                        h1("Owner Report: " + landscape.getName()),
                        br(),
                        rawHtml(writeOwnerGroups(GroupedBy.by(Component::getOwner, search)))
                )
        ).renderFormatted();
    }

    private String writeOwnerGroups(GroupedBy ownerGroups) {
        final StringBuilder builder = new StringBuilder();
        ownerGroups.getAll().forEach((owner, landscapeItems) -> {
            builder.append(
                    h2(rawHtml(owner)).attr("class", "rounded").render()
            );
            builder.append(writeGroups(GroupedBy.by(Item::getGroup, landscapeItems)).render());
        });

        return builder.toString();
    }

    private ContainerTag writeGroups(GroupedBy groups) {
        List<ContainerTag> collect = new ArrayList<>();
        groups.getAll().entrySet().forEach(entry -> collect.add(writeGroup(entry)));
        return ul().with(collect);
    }

    private ContainerTag writeGroup(Map.Entry<String, List<Item>> services) {
        return li().with(services.getValue().stream().map(this::writeItem));
    }

    private ContainerTag writeItem(Item item) {
        String groupColor = "#" + Color.nameToRGB(item.getGroup(), Color.GRAY);

        return div(rawHtml("<span style=\"color: " + groupColor + "\">&#9899;</span> " + FormatUtils.nice(item.getGroup()) + ": " + item.toString() + " (" + item.getFullyQualifiedIdentifier().toString() + ")"));

    }

}
