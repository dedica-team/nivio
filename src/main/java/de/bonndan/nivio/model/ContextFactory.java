package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ContextDescription;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class ContextFactory implements GraphNodeFactory<Context, ContextDescription, Unit> {

    public static final ContextFactory INSTANCE = new ContextFactory();

    @NonNull
    @Override
    public Context merge(@NonNull final Context existing, @NonNull final Context added) {
        ContextBuilder builder = ContextBuilder.aContext().withParent(existing.getParent());
        if (added.isAttached()) {
            builder.withParent(added.getParent());
        }
        mergeValuesIntoBuilder(existing, added, builder);
        return builder.build();
    }

    @NonNull
    @Override
    public Context createFromDescription(@NonNull final String identifier,
                                         @NonNull final Unit parent,
                                         @Nullable final ContextDescription description
    ) {
        return ContextBuilder.aContext()
                .withIdentifier(identifier)
                .withParent(parent)
                .withComponentDescription(description)
                .build();
    }
}
