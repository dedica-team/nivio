package de.bonndan.nivio.api;

import de.bonndan.nivio.assessment.AssessmentController;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.docs.DocsController;
import de.bonndan.nivio.output.map.MapController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import static de.bonndan.nivio.model.Link.LinkBuilder.linkTo;


/**
 * Factory that creates HATEOAS links.
 */
@Component
public class LinkFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkFactory.class);

    public static final String REL_SELF = "self";

    private final LocalServer localServer;

    public LinkFactory(LocalServer localServer) {
        this.localServer = localServer;
    }

    /**
     * Creates a map of {@link Link}s from a string map.
     *
     * @param links string map
     */
    public static Map<String, Link> fromStringMap(Map<String, String> links) {
        Map<String, Link> out = new HashMap<>();
        links.forEach((s, s2) -> {
            try {
                out.put(s, linkTo(new URL(s2)).build());
            } catch (MalformedURLException e) {
                LOGGER.warn("Could not convert malformed URL {} to Link", s2);
            }
        });
        return out;
    }

    public Map<String, Link> getLandscapeLinks(LandscapeImpl landscape) {
        Map<String, Link> links = new HashMap<>();
        links.put(REL_SELF, generateSelfLink(landscape));

        links.put("reindex", linkTo(localServer.getUrl(ApiController.PATH, "reindex", landscape.getIdentifier()))
                .withMedia(MediaType.APPLICATION_JSON_VALUE)
                .withTitle("Reindex the source")
                .build()
        );

        /*
         * map out put
         */
        links.put("png", linkTo(localServer.getUrl(MapController.PATH, landscape.getIdentifier(), MapController.MAP_PNG_ENDPOINT))
                .withMedia(MediaType.IMAGE_PNG_VALUE)
                .withTitle("Rendered Landscape")
                .build()
        );

        links.put("svg", linkTo(localServer.getUrl(MapController.PATH, landscape.getIdentifier(), MapController.MAP_SVG_ENDPOINT))
                .withMedia("image/svg+xml")
                .withTitle("SVG map")
                .build()
        );


        links.put("report",
                linkTo(localServer.getUrl(DocsController.PATH, landscape.getIdentifier(), DocsController.REPORT_HTML))
                        .withTitle("Written landscape report").build()
        );
        links.put("log",
                linkTo(localServer.getUrl(ApiController.PATH, "landscape", landscape.getIdentifier(), "log"))
                        .withMedia(MediaType.APPLICATION_JSON_VALUE)
                        .withTitle("Processing log")
                        .build()
        );

        links.put("assessment",
                linkTo(localServer.getUrl(AssessmentController.PATH, landscape.getFullyQualifiedIdentifier().toString()))
                        .withMedia(MediaType.APPLICATION_JSON_VALUE)
                        .withTitle("assessment")
                        .build()
        );

        return links;
    }

    private Link generateSelfLink(de.bonndan.nivio.model.Component component) {
        return linkTo(localServer.getUrl(ApiController.PATH, component.getFullyQualifiedIdentifier().jsonValue()))
                .withMedia(MediaType.APPLICATION_JSON_VALUE)
                .withTitle("JSON representation")
                .build();
    }

    /**
     * Returns the "root" api response (a list of landscapes).
     *
     * @param landscapes all landscape
     * @return the index
     */
    Index getIndex(Iterable<LandscapeImpl> landscapes) {
        Index index = new Index();

        StreamSupport.stream(landscapes.spliterator(), false)
                .forEach((LandscapeImpl landscape) -> {
                    Link link = linkTo(localServer.getUrl(ApiController.PATH, landscape.getIdentifier()))
                            .withName(landscape.getName())
                            .withRel("landscape")
                            .withMedia("application/json")
                            .build();

                    index.getLinks().put(landscape.getIdentifier(), link);
                });
        return index;
    }

    /**
     * Adds hateoas self rel links to all landscape components.
     *
     * @param landscape landscape
     */
    void setLandscapeLinksRecursive(LandscapeImpl landscape) {
        Map<String, Link> landscapeLinks = getLandscapeLinks(landscape);
        landscape.setLinks(landscapeLinks);
        setGroupSelfLinksRecursive(landscape.getGroups());
    }

    void setGroupSelfLinksRecursive(Map<String, GroupItem> groups) {
        groups.forEach((s, groupItem) -> setGroupLinksRecursive((Group) groupItem));
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
