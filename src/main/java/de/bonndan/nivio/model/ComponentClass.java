package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.*;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public enum ComponentClass {

    landscape, unit, context, group, item, part, relation; //NOSONAR

    private static final Map<Class<? extends Component>, ComponentClass> mapping = Map.ofEntries(
            Map.entry(LandscapeDescription.class, landscape),
            Map.entry(Landscape.class, landscape),
            Map.entry(UnitDescription.class, unit),
            Map.entry(Unit.class, unit),
            Map.entry(ContextDescription.class, context),
            Map.entry(Context.class, context),
            Map.entry(GroupDescription.class, group),
            Map.entry(Group.class, group),
            Map.entry(ItemDescription.class, item),
            Map.entry(Item.class, item),
            Map.entry(PartDescription.class, part),
            Map.entry(Part.class, part),
            //Map.entry(RelationDescription.class, relation),
            Map.entry(Relation.class, relation)
    );

    /**
     * Returns the class for a {@link Component}.
     *
     */
    @NonNull
    public static ComponentClass valueOf(Class<? extends Component> o) {
        return Optional.ofNullable(mapping.get(o))
                .orElseThrow(() -> new NoSuchElementException(String.format("Unknown component class for type %s", o)));
    }
}
