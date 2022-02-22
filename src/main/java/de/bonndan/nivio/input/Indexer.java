package de.bonndan.nivio.input;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.demo.PetClinicSimulatorResolver;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.external.LinkHandlerFactory;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeRepository;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

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
            ProcessLog processLog = runInputResolvers(input);
            Landscape created = applyInput(processLog, input,existing);
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
     */
    private ProcessLog runInputResolvers(LandscapeDescription input) {

        //a detailed textual log
        ProcessLog logger = new ProcessLog(LoggerFactory.getLogger(input.getIdentifier()),input.getIdentifier());

        // index all current components
        input.getIndexReadAccess().indexForSearch(Assessment.empty());

        // apply template values to items
        new TemplateResolver(logger).resolve(input);

        // read special labels on items and assign the values to fields (must be run before links resolver)
        new LabelToFieldResolver(logger).resolve(input);

        // resolve links on components to gather more data.
        new LinksResolver(logger, linkHandlerFactory).resolve(input);

        // mask any label containing secrets
        new SecureLabelsResolver(logger).resolve(input);

        //filter groups
        new GroupBlacklist(logger, input.getConfig().getGroupBlacklist()).resolve(input);

        // index all current components
        input.getIndexReadAccess().indexForSearch(Assessment.empty());

        // create relation targets on the fly if the landscape is configured "greedy"
        new InstantItemResolver(logger).resolve(input);

        // try to find "magic" relations by examining item labels for keywords and URIs
        //new LabelRelationResolver(logger, new HintFactory()).resolve(input);

        // find items for relation endpoints (which can be queries, identifiers...)
        // KEEP here (must run late after other resolvers)
        new RelationEndpointResolver(logger).resolve(input);

        // execute group "contains" queries
        new GroupQueryResolver(logger).resolve(input);

        //for simulating pet clinic events
        new PetClinicSimulatorResolver(logger).resolve(input);

        return logger;
    }

    /**
     * Creates a new {@link Landscape} and applies all input data.
     * @return new landscape
     */
    private Landscape applyInput(ProcessLog log, LandscapeDescription input, Landscape existing) {
        var processor = new InputProcessor();
        return processor.process(input, existing, log);
    }
}
