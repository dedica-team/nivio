package de.bonndan.nivio.landscape;

import org.springframework.util.StringUtils;

import java.util.Objects;

public class FullyQualifiedIdentifier {

    public static final String SEPARATOR = "|";
    private final String fqi;

    public FullyQualifiedIdentifier(String landscape, String group, String serviceIdentifier) {
        fqi = build(landscape, group, serviceIdentifier);
    }

    public FullyQualifiedIdentifier(Service service) {
        fqi = build(service.getLandscape().getIdentifier(), service.getGroup(), service.getIdentifier());
    }

    private String build(final String landscape, final String group, final String serviceIdentifier) {

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

    @Override
    public String toString() {
        return fqi;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fqi);
    }

    @Override
    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }
}
