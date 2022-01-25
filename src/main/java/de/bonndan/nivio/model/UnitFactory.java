package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.UnitDescription;
import org.springframework.lang.NonNull;

import java.util.Objects;

public class UnitFactory implements GraphNodeFactory<Unit, UnitDescription, Landscape> {

    public static final UnitFactory INSTANCE = new UnitFactory();

    @Override
    public Unit merge(@NonNull final Unit existing, Unit added) {
        UnitBuilder builder = UnitBuilder.aUnit();
        mergeIntoBuilder(existing, added, builder);
        return builder.build();
    }

    @Override
    public Unit createFromDescription(@NonNull final String identifier,
                                      @NonNull final UnitDescription description,
                                      @NonNull final Landscape landscape
    ) {
        return UnitBuilder.aUnit()
                .withIdentifier(identifier)
                .withParent(landscape)
                .withComponentDescription(Objects.requireNonNull(description))
                .build();
    }
}
