package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.FormatUtils;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.output.map.MapController;
import j2html.tags.ContainerTag;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.bonndan.nivio.output.FormatUtils.ifPresent;
import static de.bonndan.nivio.output.FormatUtils.nice;
import static de.bonndan.nivio.output.map.MapController.MAP_SVG_ENDPOINT;
import static j2html.TagCreator.*;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Generates a report containing all landscape groups and items.
 *
 *
 */
public class ReportGenerator extends HtmlGenerator {

    private static final String GROUP_CIRCLE = "&#10687;";
    private Assessment assessment;

    public ReportGenerator(LocalServer localServer, IconService iconService) {
        super(localServer, iconService);
    }

    public String toDocument(Landscape landscape) {
        return writeLandscape(landscape);
    }

    private String writeLandscape(Landscape landscape) {

        assessment = new Assessment(landscape.applyKPIs(landscape.getKpis()));
        return html(
                getHead(landscape),
                body(
                        h1(landscape.getName()),
                        iff(!isEmpty(landscape.getContact()), p("Contact: " + nice(landscape.getContact()))),
                        div(embed().attr("src", MapController.PATH + "/" + landscape.getIdentifier() + "/" + MAP_SVG_ENDPOINT).attr("class", "img-fluid img-thumbnail mx-auto d-block")),
                        br(), br(),
                        rawHtml(writeGroups(landscape))
                )
        ).renderFormatted();
    }

    private String writeGroups(Landscape landscape) {
        final StringBuilder builder = new StringBuilder();
        Map<String, Group> groups = landscape.getGroups();
        groups.forEach((s, groupItem) -> {
            String color = "#" + Color.getGroupColor(s, landscape);
            builder.append(
                    h2(rawHtml("Group: " + "<span style=\"color: " + color + "\">" + GROUP_CIRCLE + "</span> " + s))
                            .attr("class", "rounded").render()
            );
            builder.append(
                    div().attr("class", "group")
                            .with(((Group) groupItem).getItems().stream().map(this::writeItem))
                            .render()
            );
        });

        return builder.toString();
    }

    protected ContainerTag writeItem(Item item) {
        boolean hasRelations = item.getRelations().isEmpty();
        boolean hasInterfaces = item.getInterfaces().isEmpty();
        String groupColor = "#" + Color.nameToRGB(item.getGroup());

        List<ContainerTag> links = item.getLinks().entrySet().stream()
                .map(stringURLEntry -> a(" " + stringURLEntry.getKey()).attr("href", stringURLEntry.getValue().toString()))
                .collect(Collectors.toList());


        List<ContainerTag> labelList = getLabelList(item);

        List<StatusValue> statusValues = assessment.getResults().get(item.getFullyQualifiedIdentifier());

        return div(
                div(
                        iff(!isEmpty(item.getLabel(Label.note)), div(item.getLabel(Label.note)).attr("class", "alert alert-warning float float-right")),
                        a().attr("id", item.getFullyQualifiedIdentifier().toString()),
                        h3(
                                img().attr("src", iconService.getIconUrl(item)).attr("width", "30px").attr("class", "img-fluid"),
                                rawHtml(" "),
                                rawHtml(isEmpty(item.getName()) ? item.getIdentifier() : item.getName())
                        ),
                        p(FormatUtils.nice(item.getDescription())),


                        ul().with(
                                iff(!isEmpty(item.getName()), li("Name: " + FormatUtils.nice(item.getName())))
                                , iff(!isEmpty(item.getFullyQualifiedIdentifier().toString()), li("Full identifier: " + item.getFullyQualifiedIdentifier().toString()))
                                , iff(!isEmpty(item.getIdentifier()), li("Identifier: " + item.getIdentifier()))
                                , iff(!isEmpty(item.getGroup()), li(rawHtml("Group: " + "<span style=\"color: " + groupColor + "\">" + GROUP_CIRCLE + "</span> " + FormatUtils.nice(item.getGroup()))))
                                , iff(!isEmpty(item.getContact()), li("Contact: " + FormatUtils.nice(item.getContact())))
                                , iff(!isEmpty(item.getOwner()), li("Owner: " + FormatUtils.nice(item.getOwner())))
                                , iff(!isEmpty(item.getType()), li("Type: " + item.getType()))
                                , iff(links.size() > 1, li("Links: ").with(links))
                        ).with(labelList),


                        //statuses

                        iff(!statusValues.isEmpty(), h4("Status information")),
                        dl().with(
                                statusValues.stream().map(statusItem ->
                                        join(
                                                dt(FormatUtils.nice(
                                                        statusItem.getField().endsWith("." + item.getIdentifier())
                                                                ? statusItem.getField().replace("." + item.getIdentifier(), "")
                                                                : statusItem.getField()
                                                )),
                                                span(" " + statusItem.getStatus().toString() + " ")
                                                        .attr("class", "badge")
                                                        .attr("style", "background-color: " + statusItem.getStatus() + " !important"),
                                                iff(
                                                        !isEmpty(statusItem.getMessage()) && !"summary".equals(statusItem.getMessage()),
                                                        dd(span(" " + FormatUtils.nice(statusItem.getMessage())))
                                                )
                                        )
                                )
                        ),


                        //data flow
                        iff(hasRelations, h4("Relations")),
                        iff(hasRelations, ul().with(
                                item.getRelations().stream()
                                        .map(df -> {

                                            String direction = (df.getSource().equals(item)) ?
                                                    " &#10142; " : " incoming from ";
                                            Item end = (df.getSource().equals(item)) ?
                                                    df.getTarget() : df.getSource();

                                            return li(rawHtml((df.getType() != null ? df.getType() : "") + " "
                                                            + ifPresent(df.getFormat()) + " "
                                                            + ifPresent(df.getDescription())
                                                            + direction),
                                                    a(end.toString()).attr("href", "#" + end.getFullyQualifiedIdentifier()));
                                        })
                                )
                        ),

                        //interfaces
                        iff(hasInterfaces, h4("Interfaces")),
                        iff(hasInterfaces, ul().with(
                                item.getInterfaces().stream().map(interfaceItem -> li(
                                        span(interfaceItem.getDescription()),
                                        iff(!StringUtils.isEmpty(interfaceItem.getFormat()), span(", format: " + interfaceItem.getFormat())),
                                        iff(interfaceItem.getUrl() != null && !StringUtils.isEmpty(interfaceItem.getUrl().toString()),
                                                span(", ").with(a(interfaceItem.getUrl().toString()).attr("href", interfaceItem.getUrl().toString()))
                                        )
                                ))
                        ))
                ).attr("class", "card-body")

        ).attr("class", "card");
    }

    protected List<ContainerTag> getLabelList(Item item) {
        Function<Map.Entry<String, String>, Boolean> filter = s -> {
            if ("type".equals(s.getKey()) || isEmpty(s.getValue())) {
                return false;
            }
            //filter out statuses, they are part of the assessment
            return !s.getKey().startsWith(Label.status.name());
        };

        return Labeled.groupedByPrefixes(item.getLabels()).entrySet().stream()
                .filter(filter::apply)
                .map(mapEntry -> {
                    String key = StringUtils.capitalize(mapEntry.getKey());
                    if (key.equals(StringUtils.capitalize(Label.shortname.name()))) {
                        key = Label.shortname.meaning;
                    }
                    return li(key + ": " + nice(mapEntry.getValue()));
                })
                .collect(Collectors.toList());
    }

}
