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
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class OwnersReportGenerator extends HtmlGenerator {

    public OwnersReportGenerator(LocalServer localServer, IconService iconService) {
        super(localServer, iconService);
    }

    @Override
    public String toDocument(@NonNull final Landscape landscape, @NonNull final Assessment assessment, @Nullable final SearchConfig searchConfig) {

        String title = "Report";
        if (searchConfig != null && !StringUtils.isEmpty(searchConfig.getTitle())) {
            title = searchConfig.getTitle();
        }

        final Optional<String> searchTerm = searchConfig != null && !StringUtils.isEmpty(searchConfig.getSearchTerm()) ? Optional.ofNullable(searchConfig.getSearchTerm()) : Optional.empty();
        List<Item> items = new ArrayList<>(searchTerm.map(landscape::search).orElse(landscape.getItems().all()));
        return html(
                getHead(landscape),
                body(
                        h1(title),
                        h6("Landscape: " + landscape.getName()),
                        h6("Date: " + ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)),
                        iff(searchTerm.isPresent(), h6("Search term: " + searchTerm)),
                        br(),
                        rawHtml(writeOwnerGroups(GroupedBy.by(Component::getOwner, items), assessment))
                )
        ).renderFormatted();
    }

    private String writeOwnerGroups(GroupedBy ownerGroups, Assessment assessment) {
        final StringBuilder builder = new StringBuilder();
        ownerGroups.getAll().forEach((owner, items) -> {
            builder.append(
                    h2("Owner: " + owner).render()
            );
            List<ContainerTag> collect = items.stream().map(item -> div(writeItem(item, assessment)).withClass("col-sm")).collect(Collectors.toList());
            builder.append(div().withClass("row").with(collect).render());
        });

        return builder.toString();
    }

}
