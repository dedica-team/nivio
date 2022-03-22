package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ProcessDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.Process;
import de.bonndan.nivio.model.*;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Merges {@link Process}es into a {@link Landscape}
 *
 * Decorates a common {@link NodeMerger} and adds missing relations.
 */
public class ProcessMerger {

    private final NodeMerger<Process, ProcessDescription, Landscape> nodeMerger;
    private final Landscape landscape;

    public ProcessMerger(@NonNull final NodeMerger<Process, ProcessDescription, Landscape> nodeMerger,
                         @NonNull final Landscape landscape
    ) {

        this.nodeMerger = Objects.requireNonNull(nodeMerger);
        this.landscape = Objects.requireNonNull(landscape);
    }

    /**
     * Uses the common {@link NodeMerger} for the objects and the adds missing relations from process branches.
     *
     *
     */
    public ProcessingChangelog mergeAndDiff(@NonNull final List<ProcessDescription> processDescriptions,
                                            @NonNull final ProcessLog log
    ) {
        ProcessingChangelog changelog = nodeMerger.mergeAndDiff(processDescriptions, log);
        changelog.merge(addMissingRelations());
        return changelog;
    }

    private ProcessingChangelog addMissingRelations() {

        final IndexReadAccess<GraphComponent> readAccess = landscape.getReadAccess();

        ProcessingChangelog changelog = new ProcessingChangelog();
        readAccess.all(Process.class).forEach(process -> changelog.merge(addMissingRelations(process, landscape)));
        return changelog;
    }

    public static ProcessingChangelog addMissingRelations(Process process, Landscape landscape) {

        final IndexReadAccess<GraphComponent> readAccess = landscape.getReadAccess();
        final GraphWriteAccess<GraphComponent> writeAccess = landscape.getWriteAccess();
        final ProcessingChangelog changelog = new ProcessingChangelog();

        process.getBranches().stream()
                .flatMap(branch -> branch.getEdges().stream())
                .filter(uri -> readAccess.get(uri).isEmpty())
                .forEach(uri -> {
                    Item source = (Item) readAccess.get(Relation.parseSourceURI(uri)).orElseThrow();
                    Item target = (Item) readAccess.get(Relation.parseTargetURI(uri)).orElseThrow();
                    var relation = RelationFactory.create(source, target, new RelationDescription());
                    writeAccess.addOrReplaceRelation(relation);
                    changelog.addEntry(relation, ProcessingChangelog.ChangeType.CREATED);
                });

        return changelog;
    }
}
