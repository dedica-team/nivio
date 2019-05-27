package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.landscape.Groups;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.ServiceItem;
import de.bonndan.nivio.output.FormatUtils;
import de.bonndan.nivio.output.Icons;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.util.Color;
import j2html.tags.ContainerTag;
import org.springframework.util.StringUtils;

import static j2html.TagCreator.*;
import static j2html.TagCreator.a;
import static org.springframework.util.StringUtils.isEmpty;

public class ReportGenerator extends HtmlGenerator {


    public String toDocument(Landscape landscape) {

        return writeLandscape(landscape);
    }

    private String writeLandscape(Landscape landscape) {

        return html(
                getHead(landscape),
                body(
                        h1(landscape.getName()),
                        p("Contact: " + landscape.getContact()),
                        div(img().attr("src", LocalServer.url("/render/" + landscape.getIdentifier() + "/graph.png")).attr("class", "img-fluid img-thumbnail mx-auto d-block")),
                        br(), br(),
                        rawHtml(writeGroups(Groups.from(landscape)))
                )
        ).renderFormatted();
    }

    private String writeGroups(Groups groups) {
        final StringBuilder builder = new StringBuilder();
        groups.getAll().forEach((s, landscapeItems) -> {
            String color = "#" + Color.nameToRGB(s);
            builder.append(
                    h2(rawHtml("Group: " + "<span style=\"color: " + color + "\">&#9899;</span> " + s))
                            .attr("class", "rounded").render()
            );
            builder.append(
                    div().attr("class", "group").with(landscapeItems.stream().map(this::writeItem))
                            .render()
            );
        });

        return builder.toString();
    }

    private ContainerTag writeItem(ServiceItem item) {
        boolean hasDataflow = item.getDataFlow() != null && item.getDataFlow().size() > 0;
        boolean hasInterfaces = item.getInterfaces() != null && item.getInterfaces().size() > 0;
        String groupColor = "#" + Color.nameToRGB(item.getGroup());
        return div(
                div(
                        iff(!isEmpty(item.getNote()), div(item.getNote()).attr("class", "alert alert-warning float float-right")),
                        h3(
                                img().attr("src", Icons.getIcon(item).getUrl()).attr("width", "30px").attr("class", "img-fluid"),
                                rawHtml(" "),
                                rawHtml(isEmpty(item.getName()) ? item.getIdentifier() : item.getName())
                        ),
                        p(FormatUtils.nice(item.getDescription())),

                        ul().with(
                                li("Name: " + FormatUtils.nice(item.getName()))
                                , li("Full identifier: " + item.getFullyQualifiedIdentifier().toString())
                                , li("Short Name: " + FormatUtils.nice(item.getShort_name()))
                                , li(rawHtml("Group: " + "<span style=\"color: " + groupColor + "\">&#9899;</span> " + FormatUtils.nice(item.getGroup())))
                                , li("Contact: " + FormatUtils.nice(item.getContact()))
                                , li("Team: " + FormatUtils.nice(item.getTeam()))
                                , li("Owner: " + FormatUtils.nice(item.getOwner()))
                                , li("Type: " + item.getType())
                                , li("Capability: " + FormatUtils.nice(item.getCapability()))
                                , iff(!StringUtils.isEmpty(item.getHomepage()), li(span("Homepage: "), a(item.getHomepage()).attr("href", item.getHomepage())))
                                , li("Repository: " + FormatUtils.nice(item.getRepository()))
                                , li("Tags: " + FormatUtils.nice(item.getTags()))
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
                                                        img().attr("src", LocalServer.url("/icons/" + statusItem.getStatus().getSymbol() + ".png")).attr("width", "30px").attr("class", "img-fluid"),
                                                        span(" " + statusItem.getStatus().toString() + " ")
                                                                .attr("class", "badge")
                                                                .attr("style", "background-color: " + statusItem.getStatus() + " !important"),
                                                        span(" " + FormatUtils.nice(statusItem.getMessage())))
                                        )
                                )

                        ),

                        //data flow
                        iff(hasDataflow, h4("Data Flow")),
                        iff(hasDataflow, ul().with(
                                item.getDataFlow().stream().map(df -> li(rawHtml(df.getFormat() + " " + df.getDescription() + " &#10142; " + df.getTarget())))
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
