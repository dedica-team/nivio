package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.GroupDescription;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

public class GroupFactory implements GraphNodeFactory<Group, GroupDescription, Context> {

    public static final GroupFactory INSTANCE = new GroupFactory();

    @Override
    public Group merge(@NonNull final Group existing, @NonNull final Group added) {
        GroupBuilder builder = GroupBuilder.aGroup().withParent(existing.getParent());
        if (added.isAttached()) {
            builder.withParent(added.getParent());
        }
        mergeValuesIntoBuilder(existing, added, builder);

        return builder.build();
    }

    @NonNull
    @Override
    public Group createFromDescription(@NonNull final String groupIdentifier,
                                       @NonNull final Context parent,
                                       @Nullable final GroupDescription description
    ) {
        GroupBuilder builder = GroupBuilder.aGroup().withIdentifier(groupIdentifier);
        builder.withComponentDescription(description);
        builder.withParent(Objects.requireNonNull(parent, "landscape is null"));
        return builder.build();
    }
}
