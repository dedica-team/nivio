package de.bonndan.nivio.input;

import de.bonndan.nivio.input.demo.PetClinicSimulatorResolver;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.external.LinkHandlerFactory;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * This component is a wrapper around all the steps to examine and index an landscape input dto.
 */
@Component
public class Indexer {

    private final LandscapeRepository landscapeRepo;
    private final InputFormatHandlerFactory formatFactory;
    private final LinkHandlerFactory linkHandlerFactory;
    private final ApplicationEventPublisher eventPublisher;

    public Indexer(LandscapeRepository landscapeRepository,
                   InputFormatHandlerFactory formatFactory,
                   LinkHandlerFactory linkHandlerFactory,
                   ApplicationEventPublisher eventPublisher
    ) {
        this.landscapeRepo = landscapeRepository;
        this.formatFactory = formatFactory;
        this.linkHandlerFactory = linkHandlerFactory;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Indexes the given input and creates a landscape or updates an existing one.
     *
     * @param input dto
     */
    public void index(final LandscapeDescription input) {

        Landscape landscape = landscapeRepo.findDistinctByIdentifier(input.getIdentifier())
                .map(landscape1 -> LandscapeFactory.recreate(landscape1, input))
                .orElseGet(() -> {
                    Landscape created = LandscapeFactory.createFromInput(input);
                    landscapeRepo.save(created);
                    return created;
                });

        try {
            ProcessingChangelog processingChangelog = runResolvers(input, landscape);
            landscapeRepo.save(landscape);
            eventPublisher.publishEvent(new ProcessingFinishedEvent(input, landscape, processingChangelog));
            landscape.getLog().info("Reindexed landscape " + input.getIdentifier());

        } catch (ProcessingException e) {
            final String msg = "Error while reindexing landscape " + input.getIdentifier();
            landscape.getLog().warn(msg, e);
            eventPublisher.publishEvent(new ProcessingErrorEvent(input.getFullyQualifiedIdentifier(), e));
        }
    }

    private ProcessingChangelog runResolvers(LandscapeDescription input, Landscape landscape) {

        //a detailed textual log
        ProcessLog logger = landscape.getLog();

        // read all input sources
        new SourceReferencesResolver(formatFactory, logger, eventPublisher).resolve(input);

        // apply template values to items
        new TemplateResolver(logger).resolve(input);

        // resolve links on components to gather more data.
        new LinksResolver(logger, linkHandlerFactory).resolve(input);

        // mask any label containing secrets
        new SecureLabelsResolver(logger).resolve(input);

        // read special labels on items and assign the values to fields
        new LabelToFieldResolver(logger).resolve(input);

        // create relation targets on the fly if the landscape is configured "greedy"
        new InstantItemResolver(logger).resolve(input);

        // try to find "magic" relations by examining item labels for keywords and URIs
        new LabelRelationResolver(logger, new HintFactory()).resolve(input);

        // find items for relation endpoints (which can be queries, identifiers...)
        // KEEP here (must run late after other resolvers)
        new RelationEndpointResolver(logger).resolve(input);

        // execute group "contains" queries
        new GroupQueryResolver(logger).resolve(input);

        //for simulating pet clinic events
        new PetClinicSimulatorResolver(logger).resolve(input);

        //a structured log on component level
        ProcessingChangelog changelog = new ProcessingChangelog();

        // compare landscape against input, add and remove items
        changelog.merge(new DiffProcessor(logger).process(input, landscape));

        // assign items to groups, add missing groups
        changelog.merge(new GroupProcessor(logger).process(input, landscape));

        // create relations between items
        changelog.merge(new ItemRelationProcessor(logger).process(input, landscape));

        return changelog;
    }

}