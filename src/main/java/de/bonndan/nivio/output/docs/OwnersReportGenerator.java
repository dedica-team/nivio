package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.assessment.Assessment;
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
        final Optional<String> groupBy = searchConfig != null && !StringUtils.isEmpty(searchConfig.getGroupedBy()) ? Optional.ofNullable(searchConfig.getGroupedBy()) : Optional.empty();
        List<Item> items = new ArrayList<>(searchTerm.map(landscape::search).orElse(landscape.getItems().all()));
        String groupedBy = "";
        if (groupBy.isPresent() && groupBy.get().equals("owner")) {
            groupedBy = writeOwnerGroups(GroupedBy.by(Item::getOwner, items), assessment);
        } else if (groupBy.isPresent() && groupBy.get().equals("groups")) {
            groupedBy = writeGroupGroups(GroupedBy.by(item -> item.getGroup(), items), assessment);
        } else if (groupBy.isPresent() && groupBy.get().equals("labels")) {
            groupedBy = writeLabelsGroups(GroupedBy.newBy(item -> item.getLabels(), items), assessment);
        } else if (groupBy.isPresent() && groupBy.get().equals("status")) {
            groupedBy = writeStatusGroups(GroupedBy.newFunc(item -> assessment.getResults(), items), assessment);
        }

        return html(
                getHead(landscape),
                body(
                        h1(title),
                        h6("Landscape: " + landscape.getName()),
                        h6("Date: " + ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)),
                        iff(searchTerm.isPresent(), h6("Search term: " + (searchTerm.orElse(null)))),
                        br(),
                        rawHtml(groupedBy)
                )
        ).renderFormatted();
    }

    private String writeOwnerGroups(GroupedBy ownerGroups, Assessment assessment) {
        final StringBuilder builder = new StringBuilder();
        ownerGroups.getAll().forEach((owner, items) -> {
            builder.append(
                    h2("Owner: " + owner).render()
            );
            List<ContainerTag> collect = items.stream().map(item -> div(writeItem(item, assessment, items)).withClass("col-sm")).collect(Collectors.toList());
            builder.append(div().withClass("row").with(collect).render());
        });

        return builder.toString();
    }

    private String writeGroupGroups(GroupedBy groupGroups, Assessment assessment) {
        final StringBuilder builder = new StringBuilder();
        groupGroups.getAll().forEach((group, items) -> {
            builder.append(
                    h2("Group: " + group).render()
            );
            List<ContainerTag> collect = items.stream().map(item -> div(writeItem(item, assessment, items)).withClass("col-sm")).collect(Collectors.toList());
            builder.append(div().withClass("row").with(collect).render());

        });
        return builder.toString();
    }

    private String writeLabelsGroups(GroupedBy labelsGroups, Assessment assessment) {
        final StringBuilder builder = new StringBuilder();
        labelsGroups.getAll().forEach((label, items) -> {
            builder.append(
                    h2("Lifecycle: " + label).render()
            );
            List<ContainerTag> collect = items.stream().map(item -> div(writeItem(item, assessment, items)).withClass("col-sm")).collect(Collectors.toList());
            builder.append(div().withClass("row").with(collect).render());

        });
        return builder.toString();
    }

    private String writeStatusGroups(GroupedBy statusGroups, Assessment assessment) {
        final StringBuilder builder = new StringBuilder();
        statusGroups.getAll().forEach((status, items) -> {
            builder.append(
                    h2("StatusValue: " + status).render()
            );
            List<ContainerTag> collect = items.stream().map(item -> div(writeItem(item, assessment, items)).withClass("col-sm")).collect(Collectors.toList());
            builder.append(div().withClass("row").with(collect).render());

        });
        return builder.toString();
    }

}
