package de.bonndan.nivio.search;

import de.bonndan.nivio.input.dto.ContextDescription;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.UnitDescription;
import de.bonndan.nivio.model.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.bonndan.nivio.model.FullyQualifiedIdentifier.*;

/**
 * Expression to search for items in a landscape.
 */
public class ComponentMatcher {


    private String landscape;
    private String unit;
    private String context;
    private String group;
    private String item;

    private ComponentMatcher() {
    }

    public static ComponentMatcher build(
            @Nullable final String landscapeIdentifier,
            @Nullable final String unitIdentifier,
            @Nullable final String contextIdentifier,
            @Nullable final String groupIdentifier,
            @Nullable final String itemIdentifier
    ) {

        ComponentMatcher matcher = new ComponentMatcher();
        matcher.landscape = StringUtils.trimAllWhitespace(landscapeIdentifier == null ? "" : landscapeIdentifier.toLowerCase());
        if (StringUtils.hasLength(unitIdentifier))
            matcher.unit = StringUtils.trimAllWhitespace(unitIdentifier.toLowerCase());
        if (StringUtils.hasLength(contextIdentifier))
            matcher.context = StringUtils.trimAllWhitespace(contextIdentifier.toLowerCase());
        if (StringUtils.hasLength(groupIdentifier))
            matcher.group = StringUtils.trimAllWhitespace(groupIdentifier.toLowerCase());
        if (StringUtils.hasLength(itemIdentifier))
            matcher.item = StringUtils.trimAllWhitespace(itemIdentifier.toLowerCase());

        return matcher;
    }

    /**
     * Factory method to create a matcher from a string like a dataflow target.
     *
     * This is for convenience use in landscape configurations: e.g. you can refer to an item identifier without knowing
     * group or landscape.
     *
     * parts are assigned in order reverse to {@link FullyQualifiedIdentifier}: path is split, then the last element is used as item identifier
     *
     * @param string group/identifier
     * @return fqi
     * @throws IllegalArgumentException if string is empty
     */
    public static ComponentMatcher forTarget(@NonNull final String string) {
        if (!StringUtils.hasLength(string)) {
            throw new IllegalArgumentException("identifier must not be empty");
        }

        var uri = withAuthority(string);
        if (uri.isPresent()) {
            return forTarget(uri.get());
        }

        String[] split = string.split(SEPARATOR);
        if (split.length == 1) {
            return ComponentMatcher.build(null, null, null, null, split[0]);
        }

        if (split.length == 2) {
            return ComponentMatcher.build(null, null, null, split[0], split[1]);
        }

        if (split.length == 3) {
            return ComponentMatcher.build(split[0], null, null, split[1], split[2]);
        }

        if (split.length == 4) {
            return ComponentMatcher.build(split[0], null, split[1], split[2], split[3]);
        }

        if (split.length == 5) {
            return ComponentMatcher.build(split[0], split[1], split[2], split[3], split[4]);
        }

        throw new IllegalArgumentException(String.format("Given string %s contains too many parts to build an item matcher.", string));
    }

    public static ComponentMatcher forTarget(@NonNull final URI fqi) {
        return build(fqi.getAuthority(),
                getPartPath(1, fqi).orElse(null),
                getPartPath(2, fqi).orElse(null),
                getPartPath(3, fqi).orElse(null),
                getPartPath(4, fqi).orElse(null)
        );
    }

    public static <C extends Component> ComponentMatcher forTarget(String term, Class<C> cls) {

        var uri = withAuthority(term);
        if (uri.isPresent()) {
            return forTarget(uri.get());
        }

        if (Item.class.equals(cls) || ItemDescription.class.equals(cls)) {
            return forTarget(term);
        }

        if (Unit.class.equals(cls) || UnitDescription.class.equals(cls)) {
            return ComponentMatcher.build(null, term, null, null, null);
        }

        if (Context.class.equals(cls) || ContextDescription.class.equals(cls)) {
            return ComponentMatcher.build(null, null, term, null, null);
        }

        if (Group.class.equals(cls) || GroupDescription.class.equals(cls)) {
            return ComponentMatcher.build(null, null, null, term, null);
        }

        throw new IllegalArgumentException(cls + " is not supported");
    }

    private static Optional<URI> withAuthority(String string) {
        try {
            var uri = (URI.create(string));
            if (StringUtils.hasLength(uri.getAuthority())) {
                return Optional.of(uri);
            }
        } catch (IllegalArgumentException ignored) {

        }

        return Optional.empty();
    }

    public static Optional<String> getPartPath(int part, @NonNull final URI fqi) {
        var path = FullyQualifiedIdentifier.getPath(fqi).split(SEPARATOR);
        if (path.length < part || (path.length == part && !StringUtils.hasLength(path[part - 1]))) {
            return Optional.empty();
        }
        return Optional.ofNullable(path[part - 1]);
    }

    public static ComponentMatcher buildForItemAndGroup(String identifier, String group) {
        return build(null, null, null, group, identifier);
    }

    @Override
    public String toString() {

        return Stream.of(landscape, unit, context, group, item)
                .map(s -> !StringUtils.hasLength(s)? UNDEFINED : s)
                .collect(Collectors.joining("/"));
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
    public boolean isSimilarTo(@NonNull final URI other) {

        boolean equalsLandscape;
        if (!StringUtils.hasLength(landscape) || isUndefined(landscape) || FullyQualifiedIdentifier.isUndefined(other.getAuthority())) {
            equalsLandscape = true; //ignoring landscape because not set
        } else {
            equalsLandscape = landscape.equalsIgnoreCase(other.getAuthority());
        }

        if (!equalsLandscape) {
            return false;
        }

        var path = StringUtils.trimLeadingCharacter(other.getPath().toLowerCase(Locale.ROOT), SEPARATOR.charAt(0)).split(SEPARATOR);


        //check unit
        if (!equalsRegardingUndefined(path[0], unit)) return false;

        //check context
        if (!equalsRegardingUndefined(path[1], context)) return false;

        //check group
        var otherGroup = path.length > 2 ? path[2] : null;
        boolean equalsGroup;
        if (!StringUtils.hasLength(group) || FullyQualifiedIdentifier.isUndefined(otherGroup))
            equalsGroup = true;
        else if ((group.equalsIgnoreCase(Layer.domain.name()) || group.equalsIgnoreCase(Layer.infrastructure.name())) && FullyQualifiedIdentifier.isUndefined(otherGroup))
            equalsGroup = true;
        else
            equalsGroup = this.group.equalsIgnoreCase(otherGroup);

        if (!equalsGroup) {
            return false;
        }

        boolean equalsItem;
        var otherItem = path.length > 3 ? path[3] : null;
        if (!StringUtils.hasLength(this.item) || !StringUtils.hasLength(otherItem))
            equalsItem = true;
        else
            equalsItem = this.item.equalsIgnoreCase(otherItem);

        return equalsItem;
    }

    private boolean equalsRegardingUndefined(String other, String thisVal) {

        boolean equals;
        if (FullyQualifiedIdentifier.isUndefined(thisVal) || FullyQualifiedIdentifier.isUndefined(other)) {
            equals = true;
        } else {
            equals = thisVal.equalsIgnoreCase(other);
        }

        return equals;
    }

    public String getItem() {
        return item;
    }

    public String getGroup() {
        return group;
    }

    public String getContext() {
        return context;
    }

    public String getUnit() {
        return unit;
    }

    public String getLandscape() {
        return landscape;
    }
}
