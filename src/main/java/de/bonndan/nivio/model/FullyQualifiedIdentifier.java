package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonValue;
import de.bonndan.nivio.input.dto.ItemDescription;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Identifies a landscape {@link Component}.
 * <p>
 * Resembles a slash-separated path beginning with landscape name, then group, then item.
 */
public class FullyQualifiedIdentifier {

    private String landscape;
    private String group;
    private String item;

    public static final String SEPARATOR = "/";

    FullyQualifiedIdentifier() {
    }

    public static FullyQualifiedIdentifier build(
            @Nullable final String landscapeIdentifier,
            @Nullable final String groupIdentifier,
            @Nullable final String itemIdentifier
    ) {

        FullyQualifiedIdentifier fqi = new FullyQualifiedIdentifier();
        fqi.landscape = StringUtils.trimAllWhitespace(landscapeIdentifier == null ? "" : landscapeIdentifier.toLowerCase());
        if (!StringUtils.isEmpty(groupIdentifier))
            fqi.group = StringUtils.trimAllWhitespace(groupIdentifier.toLowerCase());
        if (!StringUtils.isEmpty(itemIdentifier))
            fqi.item = StringUtils.trimAllWhitespace(itemIdentifier.toLowerCase());

        return fqi;
    }

    /**
     * Builds an fqi based on a string (path).
     * <p>
     * It is assumed the path begins with the landscape identifier. This is for external use in the REST API.
     *
     * @param string raw path
     */
    public static FullyQualifiedIdentifier from(@NonNull String string) {
        if (StringUtils.isEmpty(string)) {
            throw new IllegalArgumentException("identifier must not be empty");
        }

        String[] split = string.split(SEPARATOR);
        if (split.length == 1) {
            return FullyQualifiedIdentifier.build(split[0], null, null);
        }

        if (split.length == 2) {
            return FullyQualifiedIdentifier.build(split[0], split[1], null);
        }

        if (split.length == 3) {
            return FullyQualifiedIdentifier.build(split[0], split[1], split[2]);
        }

        throw new IllegalArgumentException("Given string '" + string + "' contains too many parts to build a fqi.");
    }

    @Override
    public String toString() {

        List<String> parts = new ArrayList<>();
        parts.add(landscape);
        if (!StringUtils.isEmpty(group) || !StringUtils.isEmpty(item)) {
            parts.add(StringUtils.isEmpty(group) ? "" : group);
        }
        if (!StringUtils.isEmpty(item))
            parts.add(StringUtils.isEmpty(item) ? "" : item);

        return StringUtils.collectionToDelimitedString(parts, SEPARATOR);
    }

    /**
     * Like toString, but alway returns a complete path.
     *
     * Inserts "common" group if necessary.
     *
     * @return complete path or empty if landscape is not set
     */
    @JsonValue
    public String jsonValue() {

        if (StringUtils.isEmpty(landscape)) {
            return "";
        }

        List<String> parts = new ArrayList<>();
        parts.add(landscape);

        //need to insert "common" here if an item is referenced by the fqi
        if (!StringUtils.isEmpty(group) || !StringUtils.isEmpty(item)) {
            parts.add(StringUtils.isEmpty(group) ? Group.COMMON : group);
        }
        if (!StringUtils.isEmpty(item)) {
            parts.add(item);
        }

        return StringUtils.collectionToDelimitedString(parts, SEPARATOR);
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
     * Compares landscape items by landscape, group and identifier (ignoring case).
     *
     * @param item other item
     * @return true if group and identifier match (if group is null, it is not taken into account)
     */
    public boolean isSimilarTo(ItemDescription item) {
        FullyQualifiedIdentifier otherItemFQI = item.getFullyQualifiedIdentifier();

        boolean equalsLandscape;
        if (StringUtils.isEmpty(landscape) || StringUtils.isEmpty(otherItemFQI.landscape))
            equalsLandscape = true; //ignoring landscape because not set
        else
            equalsLandscape = landscape.equalsIgnoreCase(otherItemFQI.landscape);

        boolean equalsGroup;
        if (StringUtils.isEmpty(group) || StringUtils.isEmpty(otherItemFQI.group))
            equalsGroup = true;
        else
            equalsGroup = this.group.equalsIgnoreCase(otherItemFQI.group);

        boolean equalsItem;
        if (StringUtils.isEmpty(this.item) || StringUtils.isEmpty(otherItemFQI.item))
            equalsItem = true;
        else
            equalsItem = this.item.equalsIgnoreCase(otherItemFQI.item);

        return equalsLandscape && equalsGroup && equalsItem;
    }

    public String getItem() {
        return item;
    }

    public String getGroup() {
        return group;
    }

    public String getLandscape() {
        return landscape;
    }
}
