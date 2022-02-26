package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.GroupedBy;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.util.FrontendMapping;
import j2html.tags.ContainerTag;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class GroupingReportGenerator extends HtmlGenerator {

    public GroupingReportGenerator(LocalServer localServer, IconService iconService) {
        super(localServer, iconService);
    }

    @Override
    public String toDocument(@NonNull final Landscape landscape,
                             @NonNull final Assessment assessment,
                             @Nullable SearchConfig searchConfig,
                             @NonNull final FrontendMapping frontendMapping
    ) {

        Map<String, String> frontendMap = frontendMapping.getKeys();
        String title = "Report";
        if (searchConfig == null) {
            searchConfig = new SearchConfig(Map.of());
        }
        if (StringUtils.hasLength(searchConfig.getTitle())) {
            title = searchConfig.getTitle();
        }

        final Optional<String> searchTerm = StringUtils.hasLength(searchConfig.getSearchTerm()) ? Optional.ofNullable(searchConfig.getSearchTerm()) : Optional.empty();
        final Optional<String> reportType = StringUtils.hasLength(searchConfig.getReportType()) ? Optional.ofNullable(searchConfig.getReportType()) : Optional.empty();
        List<Item> items = new ArrayList<>(searchTerm.map(s -> landscape.getReadAccess().search(s, Item.class))
                .orElse(new ArrayList<>(landscape.getReadAccess().all(Item.class))));
        return html(
                getHead(landscape),
                body(
                        h1(title),
                        h6("Landscape: " + landscape.getName()),
                        h6("Date: " + ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)),
                        iff(searchTerm.isPresent(), h6("Search term: " + (searchTerm.orElse(null)))),
                        br(),
                        rawHtml(groupBy(reportType.orElse(""), assessment, items, frontendMap))
                )
        ).renderFormatted();
    }

    private String groupBy(String reportType, Assessment assessment, List<Item> items, Map<String, String> frontendMap) {
        switch (reportType) {
            case "owners":
                return writeGroups(GroupedBy.by(Item::getOwner, items), assessment, frontendMap.getOrDefault("Owners", "Owners"));
            case "groups":
                return writeGroups(GroupedBy.by(item -> item.getParent().getIdentifier(), items), assessment, frontendMap.getOrDefault("Groups", "Groups"));
            case "lifecycle":
                return writeGroups(GroupedBy.by(item -> item.getLabel(Label.lifecycle.name()), items), assessment, frontendMap.getOrDefault("Lifecycle", "Lifecycle"));
            case "kpis":
                return writeGroups(GroupedBy.by(item -> StatusValue.summary(
                                item.getFullyQualifiedIdentifier(),
                                assessment.getResults().getOrDefault(item.getFullyQualifiedIdentifier(), new ArrayList<>())).getStatus().toString(), items),
                        assessment,
                        frontendMap.getOrDefault("KPIs", "KPIs")
                );
            default:
                return "";
        }
    }

    private String writeGroups(GroupedBy groupedByGroups, Assessment assessment, String groupBy) {
        final StringBuilder builder = new StringBuilder();
        groupedByGroups.getAll().forEach((groupByObject, items) -> {
            builder.append(
                    h2(groupBy + ": " + groupByObject).render()
            );
            List<ContainerTag> collect = items.stream().map(item -> div(writeItem(item, assessment, items)).withClass("col-sm")).collect(Collectors.toList());
            builder.append(div().withClass("row").with(collect).render());
        });

        return builder.toString();
    }

}
