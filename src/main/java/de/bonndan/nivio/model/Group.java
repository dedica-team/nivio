package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.output.Color;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Group is a container for {@link Item}s.
 *
 * Each item can only be member of one group.
 */
public class Group implements Labeled, Linked, Assessable {

    /**
     * Default group identifier (items are assigned to this group if no group is given
     */
    @NonNull
    public static final String COMMON = "common";

    @NonNull
    private final Map<String, Link> links = new HashMap<>();

    @NonNull
    private final Map<String, String> labels = new HashMap<>();

    /**
     * Items belonging to this group. Order is important for layouting (until items are ordered there).
     */
    @NonNull
    private final Set<Item> items = new LinkedHashSet<>();

    @NonNull
    private final String identifier;
    private final String landscapeIdentifier;
    private final String owner;
    private final String description;
    private final String contact;
    private final String icon;
    private final String color;

    /**
     * @param identifier          identifier of the group, should be unique
     * @param landscapeIdentifier identifier of the landscape, will be part of fqi
     * @param owner               owner
     * @param description         description
     * @param contact             contact
     * @param icon                icon
     * @param color               color, usually member items inherit it
     */
    public Group(String identifier, String landscapeIdentifier, String owner, String description, String contact, String icon, String color) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Group identifier must not be empty");
        }
        this.identifier = identifier;

        this.landscapeIdentifier = landscapeIdentifier;
        this.owner = owner;
        this.description = description;
        this.contact = contact;
        this.icon = icon;
        this.color = color;
    }

    public Group(String identifier, String landscapeIdentifier) {
        this(identifier, landscapeIdentifier, null, null, null, null, Color.getGroupColor(identifier));
    }

    @Override
    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    @Override
    @NonNull
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(landscapeIdentifier, identifier, null);
    }

    @Override
    @NonNull
    public String getName() {
        return identifier;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    @Nullable
    public String getDescription() {
        return description;
    }

    @Override
    @Nullable
    public String getContact() {
        return contact;
    }

    @Override
    @Nullable
    public String getColor() {
        return color;
    }

    @Schema(name = "_links")
    public Map<String, Link> getLinks() {
        return links;
    }

    /**
     * Returns an immutable copy of the items.
     *
     * @return immutable copy
     */
    public Set<Item> getItems() {
        return Collections.unmodifiableSet(items);
    }

    @JsonIgnore
    @Override
    @NonNull
    public Map<String, String> getLabels() {
        return labels;
    }

    /**
     * Returns the labels without the internal ones (having prefixes).
     *
     * @return filtered labels
     */
    @JsonProperty("labels")
    public Map<String, String> getJSONLabels() {

        return Labeled.groupedByPrefixes(
                Labeled.withoutPrefixes(labels, Label.condition.name(), Label.status.name(), Tagged.LABEL_PREFIX_TAG),
                ","
        );
    }


    @Override
    @Nullable
    public String getLabel(String key) {
        return labels.get(key);
    }

    @Override
    public void setLabel(String key, String value) {
        labels.put(key, value);
    }

    @Override
    @NonNull
    public Set<StatusValue> getAdditionalStatusValues() {
        return StatusValue.fromMapping(indexedByPrefix(Label.status));
    }

    @JsonIgnore
    @Override
    @NonNull
    public List<? extends Assessable> getChildren() {
        return new ArrayList<>(getItems());
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "Group{" +
                "identifier='" + identifier + '\'' +
                '}';
    }

    /**
     * Adds an item to this group.
     *
     * @param item the item to add.
     * @throws IllegalArgumentException if the item group field mismatches
     */
    public void addItem(Item item) {
        if (!item.getGroup().equals(identifier)) {
            throw new IllegalArgumentException(String.format("Item group '%s' cannot be added to group '%s'", item.getGroup(), identifier));
        }

        items.add(item);
    }

    public String getLandscapeIdentifier() {
        return landscapeIdentifier;
    }
}
