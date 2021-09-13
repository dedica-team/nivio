package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A link.
 *
 * Used in hateoas as well as in the models.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "A link to an external resource. Contains a href (URL) plus various attributes for authentication and/or hateoas.")
public abstract class AbstractLink {

    @Schema(required = true, description = "The link target.")
    private URL href;

    private String basicAuthUsername;
    private String basicAuthPassword;

    private String headerTokenName;
    private String headerTokenValue;

    @Schema(description = "A map of arbitrary properties.")
    private final Map<String, Object> props = new HashMap<>();

    protected AbstractLink(URL href) {
        this.href = href;
    }


    public URL getHref() {
        return href;
    }

    protected void setHref(URL href) {
        this.href = href;
    }

    @JsonIgnore
    public boolean hasBasicAuth() {
        return StringUtils.hasLength(basicAuthUsername) && StringUtils.hasLength(basicAuthPassword);
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
        return StringUtils.hasLength(headerTokenName) && StringUtils.hasLength(headerTokenValue);
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

}
