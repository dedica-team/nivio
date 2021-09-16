package de.bonndan.nivio.search;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Layer;
import de.bonndan.nivio.util.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * Expression to search for items in a landscape.
 */
public class ItemMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemMatcher.class);

    private String landscape;
    private String group;
    private String item;

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
    public static Optional<ItemMatcher> forTarget(String string) {
        if (StringUtils.isEmpty(string)) {
            LOGGER.warn("identifier must not be empty");
            return Optional.empty();
        }

        if (URLFactory.getURL(string).isPresent()) {
            LOGGER.warn("ItemMatcher does not work with URLs: {}", string);
            return Optional.empty();
        }

        String[] split = string.split(FullyQualifiedIdentifier.SEPARATOR);
        if (split.length == 1) {
            return Optional.of(ItemMatcher.build(null, null, split[0]));
        }

        if (split.length == 2) {
            return Optional.of(ItemMatcher.build(null, split[0], split[1]));
        }

        if (split.length == 3) {
            return Optional.of(ItemMatcher.build(split[0], split[1], split[2]));
        }

        LOGGER.debug(String.format("Given string '%s' contains too many parts to build an item matcher.", string));
        return Optional.empty();
    }

    public static ItemMatcher forTarget(Item item) {
        return build("", item.getGroup(), item.getIdentifier());
    }

    public static ItemMatcher forTarget(FullyQualifiedIdentifier fqi) {
        return build(fqi.getLandscape(), fqi.getGroup(), fqi.getItem());
    }

    public static ItemMatcher forTarget(ItemDescription item) {
        return build("", item.getGroup(), item.getIdentifier());
    }

    @Override
    public String toString() {

        if (landscape == null) {
            return "Detached service " + super.toString();
        }

        StringBuilder b = new StringBuilder().append(landscape);
        if (StringUtils.hasLength(landscape))
            b.append(FullyQualifiedIdentifier.SEPARATOR);

        if (StringUtils.hasLength(group))
            b.append(group).append(FullyQualifiedIdentifier.SEPARATOR);
        else if (StringUtils.hasLength(landscape)) {
            b.append(FullyQualifiedIdentifier.SEPARATOR);
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
     * @param other other items FullyQualifiedIdentifier
     * @return true if group and identifier match (if group is null, it is not taken into account)
     */
    public boolean isSimilarTo(@NonNull final FullyQualifiedIdentifier other) {

        boolean equalsLandscape;
        if (!StringUtils.hasLength(landscape) || !StringUtils.hasLength(other.getLandscape())) {
            equalsLandscape = true; //ignoring landscape because not set
        } else {
            equalsLandscape = landscape.equalsIgnoreCase(other.getLandscape());
        }

        boolean equalsGroup;
        if (!StringUtils.hasLength(group) || !StringUtils.hasLength(other.getGroup()))
            equalsGroup = true;
        else if ((group.equalsIgnoreCase(Layer.domain.name()) || group.equalsIgnoreCase(Layer.infrastructure.name())) && !StringUtils.hasLength(other.getGroup()))
            equalsGroup = true;
        else
            equalsGroup = this.group.equalsIgnoreCase(other.getGroup());

        boolean equalsItem;
        if (!StringUtils.hasLength(this.item) || !StringUtils.hasLength(other.getItem()))
            equalsItem = true;
        else
            equalsItem = this.item.equalsIgnoreCase(other.getItem());

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
