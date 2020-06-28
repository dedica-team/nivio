package de.bonndan.nivio.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URL;

/**
 * Replaces org.springframework.hateoas.Link
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class HateoasLink {

    public final URL href;
    public final String hreflang;
    public final String media;
    public final String title;
    public final String type;
    public final String deprecation;
    public final String name;

    @JsonIgnore
    public final String rel;

    public HateoasLink(String rel, URL href, String hreflang, String media, String title, String type, String deprecation, String name) {
        this.rel = rel;
        this.href = href;
        this.hreflang = hreflang;
        this.media = media;
        this.title = title;
        this.type = type;
        this.deprecation = deprecation;
        this.name = name;
    }

    public static final class HateoasLinkBuilder {
        public String rel = null;
        public URL href = null;
        public String hreflang = null;
        public String media = null;
        public String title = null;
        public String type = null;
        public String deprecation = null;
        public String name = null;

        private HateoasLinkBuilder() {
        }

        public static HateoasLinkBuilder linkTo(URL href) {
            HateoasLinkBuilder hateoasLinkBuilder = new HateoasLinkBuilder();
            hateoasLinkBuilder.href = href;
            return hateoasLinkBuilder;
        }

        public HateoasLinkBuilder withRel(String rel) {
            this.rel = rel;
            return this;
        }

        public HateoasLinkBuilder withHref(URL href) {
            this.href = href;
            return this;
        }

        public HateoasLinkBuilder withHreflang(String hreflang) {
            this.hreflang = hreflang;
            return this;
        }

        public HateoasLinkBuilder withMedia(String media) {
            this.media = media;
            return this;
        }

        public HateoasLinkBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public HateoasLinkBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public HateoasLinkBuilder withDeprecation(String deprecation) {
            this.deprecation = deprecation;
            return this;
        }

        public HateoasLinkBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public HateoasLink build() {
            return new HateoasLink(rel, href, hreflang, media, title, type, deprecation, name);
        }
    }
}
