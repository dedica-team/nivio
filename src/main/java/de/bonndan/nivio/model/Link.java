package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.*;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A link.
 *
 * Used in hateoas as well as in the models.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Link {

    private String rel;
    private final URL href;
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

    private final Map<String, Object> props = new HashMap<>();

    public Link(String href) {
        if (StringUtils.isEmpty(href)) {
            this.href = null;
            return;
        }
        try {
            this.href = new URL(href);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to create link with href " + href);
        }
    }

    public Link(URL href) {
        this.href = href;
    }

    public Link(URL href, String rel) {
        this.href = href;
        this.rel = rel;
    }

    public String getRel() {
        return rel;
    }

    public URL getHref() {
        return href;
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

    @JsonIgnore
    public boolean hasBasicAuth() {
        return !StringUtils.isEmpty(basicAuthUsername) && !StringUtils.isEmpty(basicAuthPassword);
    }

    @JsonIgnore
    public String getBasicAuthUsername() {
        return basicAuthUsername;
    }

    @JsonSetter
    public void setBasicAuthUsername(String basicAuthUsername) {
        this.basicAuthUsername = basicAuthUsername;
    }

    @JsonIgnore
    public String getBasicAuthPassword() {
        return basicAuthPassword;
    }

    @JsonSetter
    public void setBasicAuthPassword(String basicAuthPassword) {
        this.basicAuthPassword = basicAuthPassword;
    }

    public boolean hasHeaderToken() {
        return !StringUtils.isEmpty(headerTokenName) && !StringUtils.isEmpty(headerTokenValue);
    }

    @JsonIgnore
    public String getHeaderTokenName() {
        return headerTokenName;
    }

    @JsonSetter
    public void setHeaderTokenName(String headerTokenName) {
        this.headerTokenName = headerTokenName;
    }

    @JsonIgnore
    public String getHeaderTokenValue() {
        return headerTokenValue;
    }

    @JsonSetter
    public void setHeaderTokenValue(String headerTokenValue) {
        this.headerTokenValue = headerTokenValue;
    }

    @JsonAnyGetter
    public Object getProperty(String key) {
        return props.get(key);
    }

    @JsonAnySetter
    public void setProperty(String key, Object value) {
        props.put(key, value);
    }

    @Override
    public String toString() {
        return "Link{" + "href=" + href + '}';
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
        private final Map<String, Object> props = new HashMap<>();

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
