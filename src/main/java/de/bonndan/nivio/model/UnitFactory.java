package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.UnitDescription;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class UnitFactory implements GraphNodeFactory<Unit, UnitDescription, Landscape> {

    public static final UnitFactory INSTANCE = new UnitFactory();

    @NonNull
    @Override
    public Unit merge(@NonNull final Unit existing, @NonNull final Unit added) {
        UnitBuilder builder = UnitBuilder.aUnit();
        mergeValuesIntoBuilder(existing, added, builder);
        return builder.build();
    }

    @NonNull
    @Override
    public Unit createFromDescription(@NonNull final String identifier,
                                      @NonNull final Landscape landscape,
                                      @Nullable final UnitDescription description
    ) {
        return UnitBuilder.aUnit()
                .withIdentifier(identifier)
                .withParent(landscape)
                .withComponentDescription(description)
                .build();
    }
}
