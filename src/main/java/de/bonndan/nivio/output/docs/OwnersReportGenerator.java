package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.GroupedBy;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.IconService;
import j2html.tags.ContainerTag;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class OwnersReportGenerator extends HtmlGenerator {

    public OwnersReportGenerator(LocalServer localServer, IconService iconService) {
        super(localServer, iconService);
    }

    @Override
    public String toDocument(@NonNull final Landscape landscape, @NonNull final Assessment assessment, @Nullable final SearchConfig searchConfig) {

        String searchTerm = searchConfig != null ? searchConfig.getSearchTerm() : null;

        List<Item> search = new ArrayList<>(searchTerm != null ? landscape.search(searchTerm) : landscape.getItems().all());
        return html(
                getHead(landscape),
                body(
                        h1("Owner Report: " + landscape.getName()),
                        br(),
                        rawHtml(writeOwnerGroups(GroupedBy.by(Component::getOwner, search), assessment))
                )
        ).renderFormatted();
    }

    private String writeOwnerGroups(GroupedBy ownerGroups, Assessment assessment) {
        final StringBuilder builder = new StringBuilder();
        ownerGroups.getAll().forEach((owner, items) -> {
            builder.append(
                    h2("Owner: " + owner).attr("class", "rounded").render()
            );
            items.forEach(item -> {
                builder.append(writeItem(item, assessment).render());
            });
        });

        return builder.toString();
    }

}
