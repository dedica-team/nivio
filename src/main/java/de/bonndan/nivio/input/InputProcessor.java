package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.*;
import de.bonndan.nivio.model.GraphComponent;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Applies a landscape description and its children to an existing landscape.
 */
public class InputProcessor {

    public Landscape process(@NonNull final LandscapeDescription input, @NonNull final Landscape existing) {

        ProcessLog log = input.getProcessLog();
        ProcessingChangelog changelog = new ProcessingChangelog();

        //recreate new landscape
        Landscape landscape = LandscapeFactory.recreate(existing.getConfiguredBuilder(), input);
        landscape.setLog(log);

        var readAccess = input.getReadAccess();

        //handle components
        changelog.merge(NodeMergerFactory.forUnits(landscape).mergeAndDiff(new ArrayList<>(readAccess.all(UnitDescription.class)), log));
        changelog.merge(NodeMergerFactory.forContexts(landscape).mergeAndDiff(new ArrayList<>(readAccess.all(ContextDescription.class)), log));
        changelog.merge(NodeMergerFactory.forGroups(landscape).mergeAndDiff(new ArrayList<>(readAccess.all(GroupDescription.class)), log));
        changelog.merge(NodeMergerFactory.forItems(landscape).mergeAndDiff(new ArrayList<>(readAccess.all(ItemDescription.class)), log));

        //update relations
        changelog.merge(NodeMergerFactory.forRelations(landscape).mergeAndDiff(new ArrayList<>(readAccess.all(ItemDescription.class)), log));

        //delete components
        if (!input.isPartial()) {
            delete(landscape, changelog);
        }
        landscape.getLog().setChangelog(changelog);
        return landscape;
    }

    private void delete(Landscape landscape, ProcessingChangelog changelog) {

        List<URI> toDelete = new ArrayList<>();
        keepComponent(landscape.getReadAccess().getRoot(), changelog.getChanges(), toDelete);


        var readAccess = landscape.getReadAccess();
        var writeAccess = landscape.getWriteAccess();
        var root = readAccess.getRoot();

        toDelete.forEach(uri -> {
            var optional = readAccess.get(uri);
            if (optional.isEmpty()) {
                landscape.getLog().warn(String.format("Cannot delete %s", uri));
                return;
            }
            GraphComponent graphComponent = optional.get();

            if (graphComponent == root) {
                return;
            }

            writeAccess.removeChild(graphComponent);
            changelog.addEntry(graphComponent, ProcessingChangelog.ChangeType.DELETED);
        });
    }

    /**
     * A component is kept if one child is kept or itself has been changed.
     */
    private boolean keepComponent(GraphComponent component, final Map<URI, ?> changes, List<URI> toDelete) {
        AtomicBoolean keepOneChild = new AtomicBoolean(false);
        component.getChildren().forEach(child -> {
            if (keepComponent(child, changes, toDelete)) keepOneChild.set(true);
        });
        var keep = keepOneChild.get() || changes.containsKey(component.getFullyQualifiedIdentifier());
        if (!keep) {
            toDelete.add(component.getFullyQualifiedIdentifier());
        }
        return keep;
    }
}
