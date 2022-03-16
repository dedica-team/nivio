package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ProcessDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.model.Process;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Merges {@link Process}es into a {@link Landscape}
 *
 *
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
    public ProcessingChangelog mergeAndDiff(@NonNull final List<ProcessDescription> processDescriptions, @NonNull final ProcessLog log) {
        ProcessingChangelog changelog = nodeMerger.mergeAndDiff(processDescriptions, log);
        addMissingRelations(changelog);
        return changelog;
    }

    private void addMissingRelations(ProcessingChangelog changelog) {

        final IndexReadAccess<GraphComponent> readAccess = landscape.getReadAccess();
        final GraphWriteAccess<GraphComponent> writeAccess = landscape.getWriteAccess();

        readAccess.all(Process.class).forEach(
                process -> process.getBranches().stream()
                        .flatMap(branch -> branch.getEdges().stream())
                        .filter(relation -> readAccess.get(relation.getFullyQualifiedIdentifier()).isEmpty())
                        .forEach(relation -> {
                            writeAccess.addOrReplaceRelation(relation);
                            changelog.addEntry(relation, ProcessingChangelog.ChangeType.CREATED);
                        })

        );
    }
}
