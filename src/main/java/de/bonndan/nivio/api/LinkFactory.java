package de.bonndan.nivio.api;

import de.bonndan.nivio.assessment.AssessmentController;
import de.bonndan.nivio.config.NivioConfigProperties;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.docs.DocsController;
import de.bonndan.nivio.output.map.MapController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import static de.bonndan.nivio.model.Link.LinkBuilder.linkTo;


/**
 * Factory that creates HATEOAS links.
 */
@Component
public class LinkFactory {

    public static final String REL_SELF = "self";

    private final LocalServer localServer;
    private final NivioConfigProperties configProperties;

    public LinkFactory(LocalServer localServer, NivioConfigProperties configProperties) {
        this.localServer = localServer;
        this.configProperties = configProperties;
    }

    public Map<String, Link> getLandscapeLinks(Landscape landscape) {
        Map<String, Link> links = new HashMap<>();
        links.put(REL_SELF, generateSelfLink(landscape));

        localServer.getUrl(ApiController.PATH, "reindex", landscape.getIdentifier()).ifPresent(url -> {
            links.put("reindex", linkTo(url)
                    .withMedia(MediaType.APPLICATION_JSON_VALUE)
                    .withTitle("Reindex the source")
                    .build()
            );
        });

        /*
         * map output
         */
        localServer.getUrl(MapController.PATH, landscape.getIdentifier(), MapController.MAP_SVG_ENDPOINT).ifPresent(url -> {
            links.put("svg", linkTo(url)
                    .withMedia("image/svg+xml")
                    .withTitle("SVG map")
                    .build()
            );
        });

        localServer.getUrl(DocsController.PATH, landscape.getIdentifier(), DocsController.REPORT_HTML).ifPresent(url -> {
            links.put("report", linkTo(url)
                    .withTitle("Written landscape report")
                    .build()
            );
        });

        localServer.getUrl(ApiController.PATH, "landscape", landscape.getIdentifier(), "log").ifPresent(url -> {
            links.put("log", linkTo(url)
                    .withMedia(MediaType.APPLICATION_JSON_VALUE)
                    .withTitle("Processing log")
                    .build()
            );
        });

        localServer.getUrl(ApiController.PATH, "landscape", landscape.getIdentifier(), "search/{lucene:query}").ifPresent(url -> {
            links.put("search", linkTo(url)
                    .withMedia(MediaType.APPLICATION_JSON_VALUE)
                    .withTitle("Search for items")
                    .build()
            );
        });


        localServer.getUrl(AssessmentController.PATH, landscape.getFullyQualifiedIdentifier().toString()).ifPresent(url -> {
            links.put("assessment", linkTo(url)
                    .withMedia(MediaType.APPLICATION_JSON_VALUE)
                    .withTitle("assessment")
                    .build()
            );
        });


        return links;
    }

    private Link generateSelfLink(de.bonndan.nivio.model.Component component) {
        return localServer.getUrl(ApiController.PATH, component.getFullyQualifiedIdentifier().jsonValue())
                .map(url -> linkTo(url)
                        .withMedia(MediaType.APPLICATION_JSON_VALUE)
                        .withTitle("JSON representation")
                        .build()
                ).orElse(null);
    }

    /**
     * Returns the "root" api response (a list of landscapes).
     *
     * @param landscapes all landscape
     * @return the index
     */
    Index getIndex(Iterable<Landscape> landscapes) {

        Index index = new Index(configProperties.getApiModel());

        StreamSupport.stream(landscapes.spliterator(), false)
                .forEach((Landscape landscape) -> {
                    localServer.getUrl(ApiController.PATH, landscape.getIdentifier()).ifPresent(url -> {
                        Link link = linkTo(url)
                                .withName(landscape.getName())
                                .withRel("landscape")
                                .withMedia("application/json")
                                .build();
                        index.getLinks().put(landscape.getIdentifier(), link);
                    });
                });
        return index;
    }

    /**
     * Adds hateoas self rel links to all landscape components.
     *
     * @param landscape landscape
     */
    void setLandscapeLinksRecursive(Landscape landscape) {
        Map<String, Link> landscapeLinks = getLandscapeLinks(landscape);
        landscape.setLinks(landscapeLinks);
        setGroupSelfLinksRecursive(landscape.getGroups());
    }

    void setGroupSelfLinksRecursive(Map<String, Group> groups) {
        groups.forEach((s, groupItem) -> setGroupLinksRecursive(groupItem));
    }

    void setGroupLinksRecursive(Group groupItem) {
        if (!groupItem.getLinks().containsKey(REL_SELF)) {
            groupItem.getLinks().put(REL_SELF, generateSelfLink(groupItem));
        }
        groupItem.getItems().forEach(this::setItemSelfLink);
    }

    void setItemSelfLink(Item item) {
        if (!item.getLinks().containsKey(REL_SELF)) {
            item.getLinks().put(REL_SELF, generateSelfLink(item));
        }
    }
}
