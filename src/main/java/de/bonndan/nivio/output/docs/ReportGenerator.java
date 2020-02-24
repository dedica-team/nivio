package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.FormatUtils;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.Color;
import j2html.tags.ContainerTag;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static de.bonndan.nivio.output.FormatUtils.ifPresent;
import static de.bonndan.nivio.output.FormatUtils.nice;
import static de.bonndan.nivio.output.map.MapController.MAP_SVG_ENDPOINT;
import static j2html.TagCreator.*;
import static j2html.TagCreator.a;
import static org.springframework.util.StringUtils.isEmpty;

public class ReportGenerator extends HtmlGenerator {

    private static final String GROUP_CIRCLE = "&#10687;";

    public ReportGenerator(LocalServer localServer) {
        super(localServer);
    }

    public String toDocument(LandscapeImpl landscape) {

        return writeLandscape(landscape);
    }

    private String writeLandscape(LandscapeImpl landscape) {

        return html(
                getHead(landscape),
                body(
                        h1(landscape.getName()),
                        p("Contact: " + nice(landscape.getContact())),
                        div(img().attr("src", "/render/" + landscape.getIdentifier() + "/" + MAP_SVG_ENDPOINT).attr("class", "img-fluid img-thumbnail mx-auto d-block")),
                        br(), br(),
                        rawHtml(writeGroups(landscape))
                )
        ).renderFormatted();
    }

    private String writeGroups(LandscapeImpl landscape) {
        final StringBuilder builder = new StringBuilder();
        Map<String, GroupItem> groups = landscape.getGroups();
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

    protected ContainerTag writeItem(LandscapeItem item) {
        boolean hasRelations = item.getRelations() != null && item.getRelations().size() > 0;
        boolean hasInterfaces = item.getInterfaces() != null && item.getInterfaces().size() > 0;
        String groupColor = "#" + Color.nameToRGB(item.getGroup());

        var links = item.getLinks().entrySet().stream()
                .map(stringURLEntry -> a(" " + stringURLEntry.getKey()).attr("href", stringURLEntry.getValue().toString()))
                .collect(Collectors.toList());
        return div(
                div(
                        iff(!isEmpty(item.getNote()), div(item.getNote()).attr("class", "alert alert-warning float float-right")),
                        a().attr("id", item.getFullyQualifiedIdentifier().toString()),
                        h3(
                                img().attr("src", localServer.getIconUrl(item)).attr("width", "30px").attr("class", "img-fluid"),
                                rawHtml(" "),
                                rawHtml(isEmpty(item.getName()) ? item.getIdentifier() : item.getName())
                        ),
                        p(FormatUtils.nice(item.getDescription())),

                        ul().with(
                                li("Name: " + FormatUtils.nice(item.getName()))
                                , li("Full identifier: " + item.getFullyQualifiedIdentifier().toString())
                                , li("Identifier: " + item.getIdentifier())
                                , li("Short Name: " + FormatUtils.nice(item.getShortName()))
                                , li(rawHtml("Group: " + "<span style=\"color: " + groupColor + "\">" + GROUP_CIRCLE + "</span> " + FormatUtils.nice(item.getGroup())))
                                , li("Contact: " + FormatUtils.nice(item.getContact()))
                                , li("Team: " + FormatUtils.nice(item.getTeam()))
                                , li("Owner: " + FormatUtils.nice(item.getOwner()))
                                , li("Type: " + item.getType())
                                , li("Capability: " + FormatUtils.nice(item.getCapability()))
                                , li("Links: ").with(links)
                                , li("Tags: " + FormatUtils.nice(item.getTags()))
                                , li("Lifecycle: " + FormatUtils.nice(item.getLifecycle() != null ? item.getLifecycle().toString() : "-"))
                                , li("Software: " + FormatUtils.nice(item.getSoftware()))
                                , li("Version: " + FormatUtils.nice(item.getVersion()))
                                , li("Machine: " + FormatUtils.nice(item.getMachine()))
                                , li("Scale: " + FormatUtils.nice(item.getScale()))
                                , li("Visibility: " + FormatUtils.nice(item.getVisibility()))
                                , li("Networks: " + FormatUtils.nice(item.getNetworks()))
                                , li("Costs: " + FormatUtils.nice(item.getCosts()))
                        ),


                        //statuses
                        iff(!item.getStatuses().isEmpty(), h4("Status information")),
                        dl().with(
                                item.getStatuses().stream().map(statusItem ->
                                        join(
                                                dt(FormatUtils.nice(statusItem.getLabel())),
                                                dd(
                                                        img().attr("src", localServer.getUrl("/icons/" + statusItem.getStatus().getSymbol() + ".png")).attr("width", "30px").attr("class", "img-fluid"),
                                                        span(" " + statusItem.getStatus().toString() + " ")
                                                                .attr("class", "badge")
                                                                .attr("style", "background-color: " + statusItem.getStatus() + " !important"),
                                                        span(" " + FormatUtils.nice(statusItem.getMessage())))
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
                                                    (Item) df.getTarget() : (Item) df.getSource();

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
                                )
                        )

                ).attr("class", "card-body")

        ).attr("class", "card");
    }

}
