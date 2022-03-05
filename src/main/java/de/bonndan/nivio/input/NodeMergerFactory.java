package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ContextDescription;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.UnitDescription;
import de.bonndan.nivio.model.*;
import org.springframework.lang.NonNull;

/**
 * A factory to create {@link NodeMerger} instances.
 */
class NodeMergerFactory {

    private NodeMergerFactory() {
    }

    static NodeMerger<Unit, UnitDescription, Landscape> forUnits(@NonNull final Landscape landscape) {
        return new NodeMerger<>(
                UnitFactory.INSTANCE,
                Landscape.class,
                Unit.class,
                landscape.getReadAccess(),
                new ParentResolver(landscape.getReadAccess(), landscape.getWriteAccess()),
                landscape.getWriteAccess()
        );
    }

    static NodeMerger<Context, ContextDescription, Unit> forContexts(@NonNull final Landscape landscape) {
        return new NodeMerger<>(
                ContextFactory.INSTANCE,
                Unit.class,
                Context.class,
                landscape.getReadAccess(),
                new ParentResolver(landscape.getReadAccess(), landscape.getWriteAccess()),
                landscape.getWriteAccess()
        );
    }

    static NodeMerger<Group, GroupDescription, Context> forGroups(@NonNull final Landscape landscape) {
        return new NodeMerger<>(
                GroupFactory.INSTANCE,
                Context.class,
                Group.class,
                landscape.getReadAccess(),
                new ParentResolver(landscape.getReadAccess(), landscape.getWriteAccess()),
                landscape.getWriteAccess()
        );
    }

    static NodeMerger<Item, ItemDescription, Group> forItems(@NonNull final Landscape landscape) {
        return new NodeMerger<>(
                ItemFactory.INSTANCE,
                Group.class,
                Item.class,
                landscape.getReadAccess(),
                new ParentResolver(landscape.getReadAccess(), landscape.getWriteAccess()),
                landscape.getWriteAccess()
        );
    }

    static EdgeMerge forRelations(Landscape landscape) {
        return new EdgeMerge(
                landscape.getReadAccess(),
                landscape.getWriteAccess()
        );
    }
}