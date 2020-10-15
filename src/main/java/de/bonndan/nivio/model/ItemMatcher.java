package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Expression to search for items in a landscape.
 *
 *
 */
public class ItemMatcher {

    private String landscape;
    private String group;
    private String item;

    public static final String SEPARATOR = "/";

    public ItemMatcher() {
    }

    public static ItemMatcher build(
            @Nullable final String landscapeIdentifier,
            @Nullable final String groupIdentifier,
            @Nullable final String itemIdentifier
    ) {

        ItemMatcher fqi = new ItemMatcher();
        fqi.landscape = StringUtils.trimAllWhitespace(landscapeIdentifier == null ? "" : landscapeIdentifier.toLowerCase());
        if (!StringUtils.isEmpty(groupIdentifier))
            fqi.group = StringUtils.trimAllWhitespace(groupIdentifier.toLowerCase());
        if (!StringUtils.isEmpty(itemIdentifier))
            fqi.item = StringUtils.trimAllWhitespace(itemIdentifier.toLowerCase());

        return fqi;
    }

    /**
     * Factory method to create a matcher from a string like a dataflow target.
     * <p>
     * This is for convenience use in landscape configurations: e.g. you can refer to an item identifier without knowing
     * group or landscape.
     * <p>
     * parts are assigned in order reverse to {@link FullyQualifiedIdentifier}: path is splitted, then the last element is used as item identifier
     *
     * @param string group/identifier
     * @return fqi
     */
    public static ItemMatcher forTarget(String string) {
        if (StringUtils.isEmpty(string)) {
            throw new IllegalArgumentException("identifier must not be empty");
        }

        String[] split = string.split(SEPARATOR);
        if (split.length == 1) {
            return ItemMatcher.build(null, null, split[0]);
        }

        if (split.length == 2) {
            return ItemMatcher.build(null, split[0], split[1]);
        }

        if (split.length == 3) {
            return ItemMatcher.build(split[0], split[1], split[2]);
        }

        throw new IllegalArgumentException("Given string '" + string + "' contains too many parts to build an item matcher.");
    }

    public static ItemMatcher forTarget(Item item) {
        return build("", item.getGroup(), item.getIdentifier());
    }

    @Override
    public String toString() {

        if (landscape == null)
            return "Detached service " + super.toString();

        StringBuilder b = new StringBuilder().append(landscape);
        if (!StringUtils.isEmpty(landscape))
            b.append(SEPARATOR);

        if (!StringUtils.isEmpty(group))
            b.append(group).append(SEPARATOR);
        else if (!StringUtils.isEmpty(landscape)) {
            b.append(SEPARATOR);
        }

        b.append(item);

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
     * Compares landscape items by landscape, group and identifier (ignoring case).
     *
     * @param fullyQualifiedIdentifier other items FullyQualifiedIdentifier
     * @return true if group and identifier match (if group is null, it is not taken into account)
     */
    public boolean isSimilarTo(FullyQualifiedIdentifier fullyQualifiedIdentifier) {

        boolean equalsLandscape;
        if (StringUtils.isEmpty(landscape) || StringUtils.isEmpty(fullyQualifiedIdentifier.getLandscape()))
            equalsLandscape = true; //ignoring landscape because not set
        else
            equalsLandscape = landscape.equalsIgnoreCase(fullyQualifiedIdentifier.getLandscape());

        boolean equalsGroup;
        if (StringUtils.isEmpty(group) || StringUtils.isEmpty(fullyQualifiedIdentifier.getGroup()))
            equalsGroup = true;
        else
            equalsGroup = this.group.equalsIgnoreCase(fullyQualifiedIdentifier.getGroup());

        boolean equalsItem;
        if (StringUtils.isEmpty(this.item) || StringUtils.isEmpty(fullyQualifiedIdentifier.getItem()))
            equalsItem = true;
        else
            equalsItem = this.item.equalsIgnoreCase(fullyQualifiedIdentifier.getItem());

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
