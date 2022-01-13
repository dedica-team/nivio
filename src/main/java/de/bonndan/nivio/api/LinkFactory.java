package de.bonndan.nivio.api;

import de.bonndan.nivio.assessment.AssessmentController;
import de.bonndan.nivio.config.NivioConfigProperties;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.docs.DocsController;
import de.bonndan.nivio.output.dto.GroupApiModel;
import de.bonndan.nivio.output.dto.ItemApiModel;
import de.bonndan.nivio.output.dto.LandscapeApiModel;
import de.bonndan.nivio.output.map.MapController;
import de.bonndan.nivio.security.AuthConfigProperties;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static de.bonndan.nivio.model.Link.LinkBuilder.linkTo;


/**
 * Factory that creates HATEOAS links.
 */
@Component
public class LinkFactory {

    public static final String REL_SELF = "self";
    public static final String AUTH_LOGIN_GITHUB = "login_github";
    public static final String LANDSCAPE = "landscape";

    private final LocalServer localServer;
    private final NivioConfigProperties configProperties;
    private final AuthConfigProperties authConfigProperties;

    public LinkFactory(LocalServer localServer,
                       NivioConfigProperties configProperties,
                       AuthConfigProperties authConfigProperties
    ) {
        this.localServer = localServer;
        this.configProperties = configProperties;
        this.authConfigProperties = authConfigProperties;
    }

    public Map<String, Link> getLandscapeLinks(LandscapeApiModel landscape) {
        Map<String, Link> links = new HashMap<>();
        generateComponentLink(landscape.getFullyQualifiedIdentifier())
                .ifPresent(link -> links.put(REL_SELF, link));

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

        localServer.getUrl(ApiController.PATH, LANDSCAPE, landscape.getIdentifier(), "log").ifPresent(url -> {
            links.put("log", linkTo(url)
                    .withMedia(MediaType.APPLICATION_JSON_VALUE)
                    .withTitle("Processing log")
                    .build()
            );
        });

        localServer.getUrl(ApiController.PATH, LANDSCAPE, landscape.getIdentifier(), "search/{lucene:query}").ifPresent(url -> {
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

    /**
     * Generates a link to a {@link de.bonndan.nivio.model.Component}
     *
     * @param fullyQualifiedIdentifier the component's fqi
     * @return link based on the {@link FullyQualifiedIdentifier}
     */
    @NonNull
    public Optional<Link> generateComponentLink(@NonNull FullyQualifiedIdentifier fullyQualifiedIdentifier) {
        return localServer.getUrl(ApiController.PATH, Objects.requireNonNull(fullyQualifiedIdentifier).jsonValue())
                .map(url -> linkTo(url)
                        .withMedia(MediaType.APPLICATION_JSON_VALUE)
                        .withTitle("JSON representation")
                        .build()
                );
    }

    /**
     * Returns the "root" api response (a list of landscapes, config and oauth2links).
     *
     * @param landscapes all landscape
     * @return the index
     */
    Index getIndex(Iterable<Landscape> landscapes) {

        Index index = new Index(getApiModel());

        StreamSupport.stream(landscapes.spliterator(), false)
                .forEach((Landscape landscape) -> {
                    localServer.getUrl(ApiController.PATH, landscape.getIdentifier()).ifPresent(url -> {
                        Link link = linkTo(url)
                                .withName(landscape.getName())
                                .withRel(LANDSCAPE)
                                .withMedia("application/json")
                                .build();
                        index.getLinks().put(landscape.getIdentifier(), link);
                    });
                });

        getAuthLinks().forEach((s, link) -> index.getOauth2Links().put(s, link));

        return index;
    }

    private ConfigApiModel getApiModel() {
        java.net.URL url = null;
        try {
            url = configProperties.getBrandingLogoUrl() != null ? new java.net.URL(configProperties.getBrandingLogoUrl()) : null;
        } catch (MalformedURLException ignored) {
            //ignored
        }
        return new ConfigApiModel(configProperties.getBaseUrl(),
                configProperties.getVersion(),
                configProperties.getBrandingForeground(),
                configProperties.getBrandingBackground(),
                configProperties.getBrandingSecondary(),
                url,
                configProperties.getBrandingMessage(),
                authConfigProperties.getLoginMode()
        );
    }

    /**
     * Returns a list of links to auth start endpoints.
     */
    public Map<String, Link> getAuthLinks() {
        Map<String, Link> map = new HashMap<>();
        Optional<URL> url = localServer.getUrl("/oauth2/authorization/github");
        url.ifPresent(url1 -> {
            Link oauth2 = linkTo(url1)
                    .withRel("github")
                    .build();
            map.put(AUTH_LOGIN_GITHUB, oauth2);
        });

        return map;
    }

    /**
     * Adds hateoas self rel links to all landscape components.
     *
     * @param landscape landscape
     */
    void setLandscapeLinksRecursive(LandscapeApiModel landscape) {
        Map<String, Link> landscapeLinks = getLandscapeLinks(landscape);
        landscape.setHateoasLinks(landscapeLinks);
        landscape.getGroups().forEach(this::setGroupLinksRecursive);
    }

    void setGroupLinksRecursive(GroupApiModel groupItem) {
        generateComponentLink(groupItem.getFullyQualifiedIdentifier())
                .ifPresent(link -> groupItem.setHateoasLinks(Map.of(REL_SELF, link)));
        groupItem.getItems().forEach(this::setItemSelfLink);
    }

    void setItemSelfLink(ItemApiModel item) {
        generateComponentLink(item.getFullyQualifiedIdentifier())
                .ifPresent(link -> item.setHateoasLinks(Map.of(REL_SELF, link)));
    }
}
