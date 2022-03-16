package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.*;
import de.bonndan.nivio.model.Process;
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
                createParentResolver(landscape),
                landscape.getWriteAccess()
        );
    }

    static NodeMerger<Context, ContextDescription, Unit> forContexts(@NonNull final Landscape landscape) {
        return new NodeMerger<>(
                ContextFactory.INSTANCE,
                Unit.class,
                Context.class,
                landscape.getReadAccess(),
                createParentResolver(landscape),
                landscape.getWriteAccess()
        );
    }

    static NodeMerger<Group, GroupDescription, Context> forGroups(@NonNull final Landscape landscape) {
        return new NodeMerger<>(
                GroupFactory.INSTANCE,
                Context.class,
                Group.class,
                landscape.getReadAccess(),
                createParentResolver(landscape),
                landscape.getWriteAccess()
        );
    }

    static NodeMerger<Item, ItemDescription, Group> forItems(@NonNull final Landscape landscape) {
        return new NodeMerger<>(
                ItemFactory.INSTANCE,
                Group.class,
                Item.class,
                landscape.getReadAccess(),
                createParentResolver(landscape),
                landscape.getWriteAccess()
        );
    }

    static EdgeMerger forRelations(@NonNull final Landscape landscape) {
        return new EdgeMerger(
                landscape.getReadAccess(),
                landscape.getWriteAccess()
        );
    }

    private static ParentResolver createParentResolver(@NonNull final Landscape landscape) {
        return new ParentResolver(
                landscape.getReadAccess(),
                landscape.getWriteAccess(),
                landscape.getConfig().getDefaultUnit(),
                landscape.getConfig().getDefaultContext()
        );
    }

    public static ProcessMerger forProcesses(@NonNull final Landscape landscape) {
        return new ProcessMerger(
                new NodeMerger<>(
                        ProcessFactory.INSTANCE,
                        Landscape.class,
                        Process.class,
                        landscape.getReadAccess(),
                        createParentResolver(landscape),
                        landscape.getWriteAccess()
                ),
                landscape
        );
    }
}
