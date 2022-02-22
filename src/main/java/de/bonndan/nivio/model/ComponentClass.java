package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.*;
import org.springframework.lang.NonNull;

import java.util.*;

public class ComponentClass {

    private ComponentClass() {
    }

    public static final Map<Class<? extends ComponentDescription>, Class<? extends GraphComponent>> mapping = Map.of(
            LandscapeDescription.class, Landscape.class,
            UnitDescription.class, Unit.class,
            ContextDescription.class, Context.class,
            GroupDescription.class, Group.class,
            ItemDescription.class, Item.class,
            PartDescription.class, Part.class
    );

    /**
     * Returns the {@link GraphComponent} implementation for a {@link ComponentDescription} dto.
     */
    public static Class<? extends GraphComponent> getComponentClass(Class<? extends ComponentDescription> o) {
        return Optional.ofNullable(mapping.get(o))
                .orElseThrow(() -> new NoSuchElementException(String.format("Unknown dto type %s", o)));
    }

    public static String getFor(@NonNull final Component component) {
        if (Objects.requireNonNull(component) instanceof ComponentDescription) {
            return Optional.ofNullable(mapping.get(component.getClass()))
                    .map(aClass -> aClass.getSimpleName().toLowerCase(Locale.ROOT))
                    .orElseThrow(() -> new NoSuchElementException(String.format("Unknown dto type %s", component.getClass())));
        }

        return component.getClass().getSimpleName().toLowerCase(Locale.ROOT);
    }
}
