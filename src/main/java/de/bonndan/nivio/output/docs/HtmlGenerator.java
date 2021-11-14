package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.FormatUtils;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.IconService;
import j2html.tags.ContainerTag;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.bonndan.nivio.model.Relation.DELIMITER;
import static de.bonndan.nivio.output.FormatUtils.ifPresent;
import static de.bonndan.nivio.output.FormatUtils.nice;
import static j2html.TagCreator.*;
import static org.springframework.util.StringUtils.hasLength;

public abstract class HtmlGenerator {

    protected static final String GROUP_CIRCLE = "&#10687;";

    private static final String CONTENT = "content";
    private static final String CLASS = "class";

    @NonNull
    protected final LocalServer localServer;

    @NonNull
    protected final IconService iconService;

    /**
     * Generates the HTML report.
     *
     * @param landscape    the landscape object
     * @param assessment   a landscape assessment
     * @param searchConfig configuration for the report
     * @return rendered html
     */
    public abstract String toDocument(@NonNull final Landscape landscape, @NonNull final Assessment assessment, @Nullable final SearchConfig searchConfig);

    protected HtmlGenerator(@NonNull final LocalServer localServer, @NonNull final IconService iconService) {
        this.localServer = Objects.requireNonNull(localServer);
        this.iconService = Objects.requireNonNull(iconService);
    }

    protected ContainerTag getHead(Landscape landscape) {

        URL css = localServer.getUrl("/css/bootstrap.min.css").orElse(null);
        return head(
                title(landscape.getName()),
                link().condAttr(css != null, "rel", "stylesheet").attr("href", css),
                meta().attr("charset", "utf-8"),
                meta().attr("name", "viewport").attr(CONTENT, "width=device-width, initial-scale=1, shrink-to-fit=no"),
                meta().attr("name", "description").attr(CONTENT, landscape.getName()),
                meta().attr("name", "author").attr(CONTENT, landscape.getContact()),
                meta().attr("generator", "author").attr(CONTENT, "nivio"),
                style("html {margin: 1rem} .group{margin-top: 1rem;} .card{margin-bottom: 1rem;}").attr("type", "text/css")
        );
    }

    protected ContainerTag writeItem(Item item, Assessment assessment, Collection<Item> allItems) {
        boolean hasRelations = !item.getRelations().isEmpty();
        boolean hasInterfaces = !item.getInterfaces().isEmpty();
        String groupColor = "#" + Color.nameToRGB(item.getGroup(), Color.GRAY);

        List<ContainerTag> links = item.getLinks().entrySet().stream()
                .map(stringURLEntry -> a(" " + stringURLEntry.getKey()).attr("href", stringURLEntry.getValue().toString()))
                .collect(Collectors.toList());


        List<ContainerTag> labelList = getLabelList(item);

        List<String> frameworks = Labeled.withPrefix(Label.framework.name(), item.getLabels()).entrySet().stream()
                .map(mapEntry -> String.format("%s: %s", StringUtils.capitalize(Label.framework.unprefixed(mapEntry.getKey())), mapEntry.getValue()))
                .collect(Collectors.toList());

        List<StatusValue> statusValues = assessment.getResults().get(item.getAssessmentIdentifier());
        if (statusValues == null) {
            statusValues = new ArrayList<>();
        }

        return div(
                div(
                        iff(hasLength(item.getLabel(Label.note)), div(item.getLabel(Label.note)).attr(CLASS, "alert alert-warning float float-right")),
                        a().attr("id", item.getFullyQualifiedIdentifier().toString()),
                        h3(
                                img().attr("src", item.getLabel(Label._icondata)).attr("width", "30px").attr(CLASS, "img-fluid"),
                                rawHtml(" "),
                                rawHtml(!hasLength(item.getName()) ? item.getIdentifier() : item.getName())
                        ),
                        p(FormatUtils.nice(item.getDescription())),


                        ul().with(
                                iff(hasLength(item.getName()), li("Name: " + FormatUtils.nice(item.getName())))
                                , iff(hasLength(item.getFullyQualifiedIdentifier().toString()), li("Full identifier: " + item.getFullyQualifiedIdentifier()))
                                , iff(hasLength(item.getIdentifier()), li("Identifier: " + item.getIdentifier()))
                                , iff(hasLength(item.getGroup()), li(rawHtml("Group: " + "<span style=\"color: " + groupColor + "\">" + GROUP_CIRCLE + "</span> " + FormatUtils.nice(item.getGroup()))))
                                , iff(hasLength(item.getContact()), li("Contact: " + FormatUtils.nice(item.getContact())))
                                , iff(hasLength(item.getOwner()), li("Owner: " + FormatUtils.nice(item.getOwner())))
                                , iff(item.getTags().length > 0, li("Tags: " + String.join(", ", item.getTags())))
                                , iff(hasLength(item.getType()), li("Type: " + item.getType()))
                                , iff(hasLength(item.getAddress()), li("Address: " + item.getAddress()))
                                , iff(links.size() > 1, li("Links: ").with(links))
                                , iff(!frameworks.isEmpty(), li("Frameworks: " + String.join(String.format("%s ", DELIMITER), frameworks)))
                        ).with(labelList),


                        //statuses
                        iff(!statusValues.isEmpty(), h4("Status")),
                        dl().with(
                                statusValues.stream().map(statusItem ->
                                        join(
                                                dt(FormatUtils.nice(
                                                                statusItem.getField().endsWith("." + item.getIdentifier())
                                                                        ? statusItem.getField().replace("." + item.getIdentifier(), "")
                                                                        : statusItem.getField()
                                                        ) + " "
                                                ).with(
                                                        span(" " + statusItem.getStatus() + " ")
                                                                .attr(CLASS, "badge")
                                                                .attr("style", "background-color: " + statusItem.getStatus() + " !important")
                                                ),
                                                iff(hasLength(statusItem.getMessage()) && !"summary".equals(statusItem.getMessage()),
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

                                            ContainerTag endTag = span(end.toString());
                                            if (allItems.contains(end)) {
                                                endTag = a(end.toString()).attr("href", "#" + end.getFullyQualifiedIdentifier());
                                            }

                                            return li(rawHtml((df.getType() != null ? df.getType() : "") + " "
                                                            + ifPresent(df.getFormat()) + " "
                                                            + ifPresent(df.getDescription())
                                                            + direction),
                                                    endTag);
                                        })
                                )
                        ),

                        //interfaces
                        iff(hasInterfaces, h4("Interfaces")),
                        iff(hasInterfaces, ul().with(
                                item.getInterfaces().stream().filter(Objects::nonNull).map(interfaceItem -> li(
                                        span(interfaceItem.getDescription()),
                                        iff(StringUtils.hasLength(interfaceItem.getFormat()), span(", format: " + interfaceItem.getFormat())),
                                        iff(interfaceItem.getUrl() != null && StringUtils.hasLength(interfaceItem.getUrl().toString()),
                                                span(", ").with(a(String.valueOf(interfaceItem.getUrl())).attr("href", String.valueOf(interfaceItem.getUrl())))
                                        )
                                ))
                        ))
                ).attr(CLASS, "card-body")

        ).attr(CLASS, "card");
    }

    protected List<ContainerTag> getLabelList(Item item) {
        Function<Map.Entry<String, String>, Boolean> filter = s -> {
            if (!hasLength(s.getValue())) {
                return false;
            }
            if (Label.framework.name().equals(s.getKey())) {
                return false;
            }
            if (s.getKey().startsWith(Label.INTERNAL_LABEL_PREFIX)) {
                return false;
            }
            if (s.getValue().equals("null")) {
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

                    return li(String.format("%s: %s", key, nice(mapEntry.getValue().replace(Labeled.PREFIX_VALUE_DELIMITER, " "))));
                })
                .collect(Collectors.toList());
    }
}