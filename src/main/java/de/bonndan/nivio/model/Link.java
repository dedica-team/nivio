package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.util.URLFactory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URL;

/**
 * A link.
 *
 * Used in hateoas as well as in the models.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "A link to an external resource. Contains a href (URL) plus various attributes for authentication and/or hateoas.")
public class Link extends AbstractLink {

    @Schema(description = "hateoas relation type")
    private String rel;

    @Schema(description = "hateoas language")
    private String hreflang;

    @Schema(description = "hateoas media type")
    private String media;

    @Schema(description = "hateoas title")
    private String title;
    private String type;

    @Schema(description = "deprecation info (typically used in OpenAPI specs)")
    private String deprecation;

    @Schema(description = "HateOAS / OpenAPI name")
    private String name;

    /**
     * String arg factory for jackson
     */
    @JsonCreator
    public static Link create(String url) {
        return new Link(URLFactory.getURL(url).orElse(null));
    }

    public Link() {
        super(null);
    }

    public Link(URL href) {
        super(href);
    }

    public Link(URL href, String rel) {
        super(href);
        this.rel = rel;
    }

    public String getRel() {
        return rel;
    }

    public String getHreflang() {
        return hreflang;
    }

    public String getMedia() {
        return media;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDeprecation() {
        return deprecation;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Link{" + "href=" + getHref() + '}';
    }

    public static final class LinkBuilder {
        private String rel;
        private URL href;
        private String hreflang;
        private String media;
        private String title;
        private String type;
        private String deprecation;
        private String name;
        private String basicAuthUsername;
        private String basicAuthPassword;
        private String headerTokenName;
        private String headerTokenValue;

        private LinkBuilder() {
        }

        public static LinkBuilder linkTo(URL href) {
            return linkTo().withHref(href);
        }

        public static LinkBuilder linkTo() {
            return new LinkBuilder();
        }

        public LinkBuilder withRel(String rel) {
            this.rel = rel;
            return this;
        }

        public LinkBuilder withHref(URL href) {
            this.href = href;
            return this;
        }

        public LinkBuilder withHreflang(String hreflang) {
            this.hreflang = hreflang;
            return this;
        }

        public LinkBuilder withMedia(String media) {
            this.media = media;
            return this;
        }

        public LinkBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public LinkBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public LinkBuilder withDeprecation(String deprecation) {
            this.deprecation = deprecation;
            return this;
        }

        public LinkBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public LinkBuilder withBasicAuthUsername(String basicAuthUsername) {
            this.basicAuthUsername = basicAuthUsername;
            return this;
        }

        public LinkBuilder withBasicAuthPassword(String basicAuthPassword) {
            this.basicAuthPassword = basicAuthPassword;
            return this;
        }

        public LinkBuilder withHeaderTokenName(String headerTokenName) {
            this.headerTokenName = headerTokenName;
            return this;
        }

        public LinkBuilder withHeaderTokenValue(String headerTokenValue) {
            this.headerTokenValue = headerTokenValue;
            return this;
        }

        public Link build() {
            Link link = new Link(href, rel);
            link.setBasicAuthUsername(basicAuthUsername);
            link.setBasicAuthPassword(basicAuthPassword);
            link.setHeaderTokenName(headerTokenName);
            link.setHeaderTokenValue(headerTokenValue);
            link.hreflang = this.hreflang;
            link.deprecation = this.deprecation;
            link.title = this.title;
            link.name = this.name;
            link.media = this.media;
            link.type = this.type;
            return link;
        }
    }
}
