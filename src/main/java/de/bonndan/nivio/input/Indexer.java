package de.bonndan.nivio.input;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.demo.PetClinicSimulatorResolver;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.external.LinkHandlerFactory;
import de.bonndan.nivio.input.hints.HintFactory;
import de.bonndan.nivio.input.hints.HintResolver;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeRepository;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * This component is a wrapper around all the steps to examine and index a landscape input dto.
 */
@Component
public class Indexer {

    private final LandscapeRepository landscapeRepo;
    private final LinkHandlerFactory linkHandlerFactory;
    private final ApplicationEventPublisher eventPublisher;

    public Indexer(LandscapeRepository landscapeRepository,
                   LinkHandlerFactory linkHandlerFactory,
                   ApplicationEventPublisher eventPublisher
    ) {
        this.landscapeRepo = landscapeRepository;
        this.linkHandlerFactory = linkHandlerFactory;
        this.eventPublisher = eventPublisher;
    }

    @EventListener(IndexEvent.class)
    public void onIndexEvent(@NonNull final IndexEvent event) {
        event.getLandscapeDescriptions().forEach(this::index);
        event.getSeedConfiguration().ifPresent(
                seedConfiguration -> eventPublisher.publishEvent(new SeedConfigurationProcessedEvent(seedConfiguration))
        );
    }

    /**
     * Indexes the given input and creates a landscape or updates an existing one.
     *
     * @param input dto
     */
    public void index(@NonNull final LandscapeDescription input) {

        Landscape existing = landscapeRepo.findDistinctByIdentifier(input.getIdentifier())
                .orElseGet(() -> {
                    Landscape created = LandscapeFactory.createIntermediate(input.getIdentifier());
                    landscapeRepo.save(created);
                    return created;
                });
        try {
            Landscape created = applyInput(runInputResolvers(input), existing);
            landscapeRepo.save(created);
            eventPublisher.publishEvent(new ProcessingFinishedEvent(input, created, created.getLog().getChangelog()));
            created.getLog().info(String.format("Reindexed landscape %s", input.getIdentifier()));

        } catch (ProcessingException e) {
            final String msg = "Error while reindexing landscape " + input.getIdentifier();
            existing.getLog().warn(msg, e);
            eventPublisher.publishEvent(new ProcessingErrorEvent(input.getFullyQualifiedIdentifier(), e));
        }
    }

    /**
     * mutates and enhances the given input
     *
     * @param in unresolved dto
     * @return dto with resolved data and transformations
     */
    private LandscapeDescription runInputResolvers(final LandscapeDescription in) {

        //a detailed textual log
        ProcessLog logger = new ProcessLog(LoggerFactory.getLogger(in.getIdentifier()), in.getIdentifier());
        in.setProcessLog(logger);

        // index all current components
        in.getReadAccess().indexForSearch(Assessment.empty());

        return Stream.of(in)
                .map(input -> {
                    // apply template values to items
                    // reindex because parent identifiers might have been set
                    return new TemplateResolver().resolve(input);
                })
                .map(input -> {
                    // read special labels on items and assign the values to fields (must be run before links resolver)
                    // reindex because parent identifiers might have been set
                    return new LabelToFieldResolver().resolve(input);
                })
                .map(input -> {
                    // resolve links on components to gather more data.
                    return new LinksResolver(linkHandlerFactory).resolve(input);
                })
                .map(input -> {
                    // mask any label containing secrets
                    return new SecureLabelsResolver().resolve(input);
                })
                .map(input -> {
                    //filter groups, reindex because components might have been removed
                    return new GroupBlacklist().resolve(input);
                })
                .map(input -> {
                    // find items for relation endpoints (which can be queries, identifiers...)
                    // KEEP here (must run late after other resolvers)
                    return new RelationEndpointResolver().resolve(input);
                })
                .map(input -> {
                    // execute group "contains" queries
                    return new ContainsResolver().resolve(input);
                })
                .map(input -> {
                    //add hints concerning possible items, does not modify the input
                    return new HintResolver(new HintFactory()).resolve(input);
                })
                .map(input -> {
                    //for simulating pet clinic events
                    return new PetClinicSimulatorResolver().resolve(input);
                }).findFirst().orElseThrow();
    }

    /**
     * Creates a new {@link Landscape} and applies all input data.
     *
     * @return new landscape
     */
    private Landscape applyInput(LandscapeDescription input, Landscape existing) {
        var processor = new InputProcessor();
        return processor.process(input, existing);
    }
}
