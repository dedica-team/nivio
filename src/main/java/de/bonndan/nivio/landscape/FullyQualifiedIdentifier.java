package de.bonndan.nivio.landscape;

import org.springframework.util.StringUtils;

import java.util.Objects;

public class FullyQualifiedIdentifier {

    private String landscape;
    private String group;
    private String identifier;

    public static final String SEPARATOR = "/";

    public FullyQualifiedIdentifier() {
    }

    public static FullyQualifiedIdentifier build(final String landscape, final String group, final String serviceIdentifier) {

        if (StringUtils.isEmpty(serviceIdentifier))
            throw new IllegalArgumentException("serviceIdentifier must not be empty");

        FullyQualifiedIdentifier fqi = new FullyQualifiedIdentifier();
        fqi.landscape = StringUtils.trimAllWhitespace(landscape == null ? "" : landscape.toLowerCase());
        if (!StringUtils.isEmpty(group))
            fqi.group = StringUtils.trimAllWhitespace(group.toLowerCase());
        fqi.identifier = StringUtils.trimAllWhitespace(serviceIdentifier.toLowerCase());

        return fqi;
    }

    /**
     * Factory method to create a fqi from a string like a dataflow target.
     *
     * @param string group/identifier
     * @return fqi
     */
    public static FullyQualifiedIdentifier from(String string) {
        if (StringUtils.isEmpty(string))
            throw new IllegalArgumentException("identifier must not be empty");

        String[] split = string.split(SEPARATOR);
        if (split.length == 1)
            return FullyQualifiedIdentifier.build(null, null, split[0]);

        if (split.length == 2)
            return FullyQualifiedIdentifier.build(null, split[0], split[1]);

        if (split.length == 3)
            return FullyQualifiedIdentifier.build(split[0], split[1], split[3]);

        return null;
    }

    @Override
    public String toString() {
        if (landscape == null || StringUtils.isEmpty(identifier))
            return "Detached service " + super.toString();

        StringBuilder b = new StringBuilder().append(landscape);
        if (!StringUtils.isEmpty(landscape))
            b.append(SEPARATOR);

        if (!StringUtils.isEmpty(group))
            b.append(group).append(SEPARATOR);

        b.append(identifier);

        return b.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }

    /**
     * Compares landscape items by group and identifier.
     *
     * @param item other item
     * @return true if group and identifier match (if group is null, it is not taken into account)
     */
    public boolean isSimilarTo(ServiceItem item) {
        FullyQualifiedIdentifier fqi = item.getFullyQualifiedIdentifier();
        if (StringUtils.isEmpty(group) || StringUtils.isEmpty(item.getGroup()))
            return identifier.equals(fqi.identifier);

        return group.equals(fqi.group) && identifier.equals(fqi.identifier);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getGroup() {
        return group;
    }

    public String getLandscape() {
        return landscape;
    }
}
