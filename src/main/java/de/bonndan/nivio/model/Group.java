package de.bonndan.nivio.model;

import de.bonndan.nivio.output.Color;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;

import static de.bonndan.nivio.model.ComponentDiff.compareCollections;
import static de.bonndan.nivio.model.ComponentDiff.compareStrings;

/**
 * Group is a container for {@link Item}s.
 *
 * Each item can only be member of one group.
 */
public class Group implements Component, Labeled, Linked {

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
    private final Set<FullyQualifiedIdentifier> items = new LinkedHashSet<>();

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
    public Group(@NonNull final String identifier,
                 @NonNull final String landscapeIdentifier,
                 @Nullable final String owner,
                 @Nullable final String description,
                 @Nullable final String contact,
                 @Nullable final String icon,
                 @Nullable final String color
    ) {
        if (!StringUtils.hasLength(identifier)) {
            throw new IllegalArgumentException("Group identifier must not be empty");
        }
        this.identifier = identifier;

        if (!StringUtils.hasLength(landscapeIdentifier)) {
            throw new IllegalArgumentException("Landscape identifier must not be empty");
        }
        this.landscapeIdentifier = landscapeIdentifier;
        this.owner = owner;
        this.description = description;
        this.contact = contact;
        this.icon = icon;
        this.color = Color.safe(color);
    }

    public Group(@NonNull final String identifier, @NonNull final String landscapeIdentifier) {
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
     * Returns a copy of the items.
     *
     * @return immutable copy
     */
    public Set<FullyQualifiedIdentifier> getItems() {
        return new LinkedHashSet<>(items);
    }

    @Override
    @NonNull
    public Map<String, String> getLabels() {
        return labels;
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
    public void addOrReplaceItem(Item item) {
        if (!identifier.equals(item.getGroup())) {
            throw new IllegalArgumentException(String.format("Item group '%s' cannot be added to group '%s'", item.getGroup(), identifier));
        }

        //ensures that an existing item is removed from set
        FullyQualifiedIdentifier fqi = item.getFullyQualifiedIdentifier();
        items.stream().filter(item1 -> item1.equals(fqi)).findFirst().ifPresent(items::remove);
        items.add(fqi);
    }

    /**
     * Removes an item from a group.
     *
     * @param item the item to remove
     * @return true if the item could be removed
     */
    public boolean removeItem(@Nullable Item item) {
        if (item == null) {
            return false;
        }
        return items.remove(item.getFullyQualifiedIdentifier());
    }

    public String getLandscapeIdentifier() {
        return landscapeIdentifier;
    }

    /**
     * Compare on field level against a newer version.
     *
     * @param newer the newer version
     * @return a list of changes if any changes are present
     * @throws IllegalArgumentException if the arg is not comparable
     */
    public List<String> getChanges(Group newer) {
        if (!newer.getIdentifier().equalsIgnoreCase(this.identifier)) {
            throw new IllegalArgumentException(String.format("Cannot compare group %s against %s", newer.getIdentifier(), this.getIdentifier()));
        }

        List<String> changes = new ArrayList<>();
        changes.addAll(compareStrings(this.contact, newer.contact, "Contact"));
        changes.addAll(compareStrings(this.description, newer.description, "Description"));
        changes.addAll(compareStrings(this.owner, newer.owner, "Owner"));
        changes.addAll(compareStrings(this.color, newer.color, "Color"));
        changes.addAll(newer.diff(this));
        changes.addAll(compareCollections(this.links.keySet(), newer.links.keySet(), "Links"));

        return changes;
    }
}
