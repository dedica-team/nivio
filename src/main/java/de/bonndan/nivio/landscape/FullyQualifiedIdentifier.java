package de.bonndan.nivio.landscape;

import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

public class FullyQualifiedIdentifier {

    private String landscapeId;

    private String identifier;

    private String group;


    public static final String SEPARATOR = "|";

    public FullyQualifiedIdentifier() {
    }

    public FullyQualifiedIdentifier(String landscapeId, String identifier, String group) {
        this.landscapeId = landscapeId;
        this.identifier = identifier;
        this.group = group;
    }

    public static String build(final String landscape, final String group, final String serviceIdentifier) {

        if (StringUtils.isEmpty(landscape))
            throw new IllegalArgumentException("landscape must not be empty");
        if (StringUtils.isEmpty(group))
            throw new IllegalArgumentException("group must not be empty");
        if (StringUtils.isEmpty(serviceIdentifier))
            throw new IllegalArgumentException("serviceIdentifier must not be empty");

        return StringUtils.trimAllWhitespace(landscape.toLowerCase())
                + SEPARATOR
                + StringUtils.trimAllWhitespace(group.toLowerCase())
                + SEPARATOR
                + StringUtils.trimAllWhitespace(serviceIdentifier.toLowerCase());
    }

    public String getLandscapeId() {
        return landscapeId;
    }

    public void setLandscapeId(String landscapeId) {
        this.landscapeId = landscapeId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    private String fqi() {
        return build(landscapeId, group, identifier);
    }

    @Override
    public String toString() {
        return fqi();
    }

    @Override
    public int hashCode() {
        return Objects.hash(fqi());
    }

    @Override
    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }
}
