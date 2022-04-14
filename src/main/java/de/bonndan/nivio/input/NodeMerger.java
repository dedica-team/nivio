package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.model.*;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Applies updates from input dtos to models.
 *
 * Resolves missing parents, but does not resolve relations.
 *
 * @param <T> type of the model (e.g. {@link Unit})
 * @param <D> type of the DTO
 * @param <P> type of the parent
 */
class NodeMerger<T extends GraphComponent, D extends ComponentDescription, P extends GraphComponent> {

    @NonNull
    private final GraphNodeFactory<T, D, P> factory;
    @NonNull
    private final Class<P> pClass;
    @NonNull
    private final Class<T> tClass;
    @NonNull
    private final IndexReadAccess<GraphComponent> indexReadAccess;
    @NonNull
    private final ParentResolver parentResolver;
    @NonNull
    private final GraphWriteAccess<GraphComponent> graphWriteAccess;

    NodeMerger(@NonNull final GraphNodeFactory<T, D, P> factory,
               @NonNull final Class<P> pClass,
               @NonNull final Class<T> tClass,
               @NonNull final IndexReadAccess<GraphComponent> indexReadAccess,
               @NonNull final ParentResolver parentResolver,
               @NonNull final GraphWriteAccess<GraphComponent> graphWriteAccess
    ) {
        this.factory = factory;
        this.pClass = pClass;
        this.tClass = tClass;
        this.indexReadAccess = indexReadAccess;
        this.parentResolver = parentResolver;
        this.graphWriteAccess = graphWriteAccess;
    }

    /**
     * @param inputNodes all input {@link ComponentDescription}s
     * @param processLog factory to create new nodes
     * @return log of changes
     */
    public ProcessingChangelog mergeAndDiff(@NonNull final List<D> inputNodes, @NonNull final ProcessLog processLog) {

        ProcessingChangelog changelog = new ProcessingChangelog();

        /*
         * add or update nodes from input dto
         */
        inputNodes.forEach(dto -> {
            Optional<T> existing = indexReadAccess.matchOneByIdentifiers(dto.getIdentifier(), dto.getParentIdentifier(), tClass);
            P parent = existing.map(t -> (P) t.getParent()).orElseGet(() -> parentResolver.getParent(dto, pClass));
            T fromDescription = factory.createFromDescription(dto.getIdentifier(), parent, dto);

            T added = existing.map(component -> factory.merge(component, fromDescription)).orElse(fromDescription);

            graphWriteAccess.addOrReplaceChild(added);

            if (existing.isEmpty()) {
                processLog.info(String.format("Adding %s", added.getFullyQualifiedIdentifier()));
                changelog.addEntry(added, ProcessingChangelog.ChangeType.CREATED);
            } else {
                processLog.info(String.format("Updating %s", added.getFullyQualifiedIdentifier()));
                changelog.addEntry(added, ProcessingChangelog.ChangeType.UPDATED, existing.get().getChanges(added));
            }

            markParentsAsUpdated(added, changelog);
        });

        return changelog;
    }

    /**
     * Marks each ancestor as updated
     */
    private void markParentsAsUpdated(@NonNull GraphComponent component, @NonNull final ProcessingChangelog changelog) {
        if (!StringUtils.hasLength(component.getParentIdentifier())) {
            return;
        }
        markParentsAsUpdated(component.getParent(), changelog);
        changelog.addEntry(
                component.getParent(),
                ProcessingChangelog.ChangeType.UPDATED,
                List.of(String.format("Updated %s", component.getFullyQualifiedIdentifier()))
        );
    }
}
