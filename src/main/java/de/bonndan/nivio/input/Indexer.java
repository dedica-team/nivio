
package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Indexer {

    private static final Logger _logger = LoggerFactory.getLogger(Indexer.class);

    private final LandscapeRepository landscapeRepo;
    private final ItemDescriptionFormatFactory formatFactory;
    private final ApplicationEventPublisher eventPublisher;

    public Indexer(LandscapeRepository landscapeRepository,
                   ItemDescriptionFormatFactory formatFactory,
                   ApplicationEventPublisher eventPublisher
    ) {
        this.landscapeRepo = landscapeRepository;
        this.formatFactory = formatFactory;
        this.eventPublisher = eventPublisher;
    }

    public ProcessLog reIndex(final LandscapeDescription input) {

        ProcessLog logger = new ProcessLog(_logger);

        LandscapeImpl landscape = landscapeRepo.findDistinctByIdentifier(input.getIdentifier()).orElseGet(() -> {
            logger.info("Creating new landscape " + input.getIdentifier());
            LandscapeImpl landscape1 = LandscapeFactory.create(input);
            landscapeRepo.save(landscape1);
            return landscape1;
        });
        LandscapeFactory.assignAll(input, landscape);
        logger.setLandscape(landscape);

        try {
            runResolvers(input, logger, landscape);
            landscapeRepo.save(landscape);
        } catch (ProcessingException e) {
            final String msg = "Error while reindexing landscape " + input.getIdentifier();
            logger.warn(msg, e);
            eventPublisher.publishEvent(new ProcessingErrorEvent(this, e));
        }

        eventPublisher.publishEvent(new ProcessingFinishedEvent(input, landscape));
        logger.info("Reindexed landscape " + input.getIdentifier());
        landscape.setProcessLog(logger);
        return logger;
    }

    private void runResolvers(LandscapeDescription input, ProcessLog logger, LandscapeImpl landscape) {
        Map<ItemDescription, List<String>> templatesAndTargets = new HashMap<>();

        // 1. read all input sources
        new SourceReferencesResolver(formatFactory, logger).resolve(input, templatesAndTargets);

        // 2. apply template values to the items
        new TemplateResolver().processTemplates(input, templatesAndTargets);

        // 3. read special labels on items and assign the values to fields
        new LabelToFieldProcessor(logger).process(input, landscape);

        // 4. create relation targets on the fly if the landscape is configured "greedy"
        new InstantItemResolver(logger).processTargets(input);

        // 5. find items for relation endpoints (which can be queries, identifiers...)
        new RelationEndpointResolver(logger).processRelations(input);

        // 6. add any missing groups
        new GroupResolver(logger).process(input, landscape);

        // 7. compare landscape against input, add and remove items
        new DiffResolver(logger).process(input, landscape);

        // 8. execute group "contains" queries
        new GroupQueryResolver(logger).process(input, landscape);

        // 9. try to find "magic" relations by examining item labels for keywords
        new MagicLabelRelations(logger).process(input, landscape);

        // 10. create relations between items
        new ItemRelationResolver(logger).process(input, landscape);
    }

}