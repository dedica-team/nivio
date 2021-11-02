package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.output.Color;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static de.bonndan.nivio.util.SafeAssign.assignSafeIfAbsent;

public class GroupFactory {

    /**
     * Merges all absent values from the second param into the first.
     *
     * @param group copy base
     * @param added group that carries additional values
     * @return a new group, or the first one if the second one is null
     */
    public static Group merge(final Group group, final Group added) {

        GroupBuilder builder = getBuilder(group);

        if (added != null) {
            assignSafeIfAbsent(added.getColor(), group.getColor(), builder::withColor);
            assignSafeIfAbsent(added.getContact(), group.getContact(), builder::withContact);
            assignSafeIfAbsent(added.getDescription(), group.getDescription(), builder::withDescription);
            assignSafeIfAbsent(added.getOwner(), group.getOwner(), builder::withOwner);

            added.getLinks().forEach((s, url) -> group.getLinks().putIfAbsent(s, url));
            added.getLabels().forEach((s, val) -> builder.getLabels().putIfAbsent(s, val));
        }

        if (StringUtils.isEmpty(builder.getColor())) {
            builder.withColor(Color.getGroupColor(builder.getIdentifier()));
        }

        return builder.build();
    }

    public static Group createFromDescription(@NonNull final String groupIdentifier,
                                              @NonNull final String landscapeIdentifier,
                                              @Nullable final GroupDescription description
    ) {
        GroupBuilder builder = getBuilder(new Group(groupIdentifier, landscapeIdentifier));

        if (description != null) {
            builder.withColor(description.getColor());
            builder.withContact(description.getContact());
            builder.withOwner(description.getOwner());
            builder.withDescription(description.getDescription());
            description.getLinks().forEach((s, url) -> builder.getLinks().putIfAbsent(s, url));
            description.getLabels().forEach((s, val) -> builder.getLabels().putIfAbsent(s, val));
        }


        if (StringUtils.hasLength(builder.getColor())) {
            builder.withColor(Color.getGroupColor(builder.getIdentifier()));
        }

        return builder.build();
    }

    private static GroupBuilder getBuilder(Group group) {
        Objects.requireNonNull(group, "Group is null");
        return GroupBuilder.aGroup()
                .withIdentifier(group.getIdentifier())
                .withDescription(group.getDescription())
                .withOwner(group.getOwner())
                .withContact(group.getContact())
                .withColor(group.getColor())
                .withLandscapeIdentifier(group.getLandscapeIdentifier())
                .withLinks(group.getLinks())
                .withLabels(group.getLabels());
    }
}
