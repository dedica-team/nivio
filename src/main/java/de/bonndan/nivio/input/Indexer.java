
package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Indexer {

    private static final Logger _logger = LoggerFactory.getLogger(Indexer.class);

    private final LandscapeRepository landscapeRepo;
    private final ItemDescriptionFormatFactory formatFactory;
    private final NotificationService notificationService;


    public Indexer(LandscapeRepository landscapeRepository,
                   ItemDescriptionFormatFactory formatFactory,
                   NotificationService notificationService
    ) {
        this.landscapeRepo = landscapeRepository;
        this.formatFactory = formatFactory;
        this.notificationService = notificationService;
    }

    public ProcessLog reIndex(final LandscapeDescription input) {

        ProcessLog logger = new ProcessLog(_logger);

        LandscapeImpl landscape = landscapeRepo.findDistinctByIdentifier(input.getIdentifier()).orElseGet(() -> {
            logger.info("Creating new landscape " + input.getIdentifier());
            LandscapeImpl landscape1 = input.toLandscape();
            landscapeRepo.save(landscape1);
            return landscape1;
        });

        landscape.setName(input.getName());
        landscape.setContact(input.getContact());
        landscape.setConfig(input.getConfig());
        logger.setLandscape(landscape);

        try {
            Map<ItemDescription, List<String>> templatesAndTargets = new HashMap<>();
            new SourceReferencesResolver(formatFactory, logger).resolve(input, templatesAndTargets);
            new TemplateResolver().processTemplates(input, templatesAndTargets);
            new InstantItemResolver(logger).processTargets(input);
            new MagicLabelRelations().process(input, landscape);
            new RelationResolver(logger).processRelations(input);
            new GroupResolver(logger).process(input, landscape);

            diff(input, landscape, logger);
            fillGroups(input, landscape);
            linkItems(input, landscape, logger);
            landscapeRepo.save(landscape);
        } catch (ProcessingException e) {
            final String msg = "Error while reindexing landscape " + input.getIdentifier();
            logger.warn(msg, e);
            notificationService.sendError(e, msg);
        }

        logger.info("Reindexed landscape " + input.getIdentifier());
        return logger;
    }

    private void diff(final LandscapeDescription input, final LandscapeImpl landscape, ProcessLog logger) {

        Set<Item> existingItems = landscape.getItems();

        //insert new ones
        List<LandscapeItem> newItems = added(input.getItemDescriptions(), existingItems);
        Set<Item> inLandscape = new HashSet<>();
        logger.info("Adding " + newItems.size() + " items in env " + landscape.getIdentifier());
        newItems.forEach(
                serviceDescription -> {
                    logger.info("Creating new service " + serviceDescription.getIdentifier() + " in env " + input.getIdentifier());
                    Item created = ItemFactory.fromDescription(serviceDescription, landscape);
                    landscape.addItem(created);
                    inLandscape.add(created);
                }
        );

        //update existing
        List<LandscapeItem> kept = new ArrayList<>();
        if (input.isPartial()) {
            kept.addAll(existingItems); //we want to keep all, increment does not contain all items
        } else {
            kept = kept(input.getItemDescriptions(), existingItems);
        }
        logger.info("Updating " + kept.size() + " items in landscape " + landscape.getIdentifier());
        kept.forEach(
                item -> {

                    ItemDescription description = (ItemDescription) Items.find(item.getFullyQualifiedIdentifier(), input.getItemDescriptions()).orElse(null);
                    if (description == null) {
                        if (input.isPartial()) {
                            inLandscape.add((Item) item);
                            return;
                        } else {
                            throw new ProcessingException(input, "Item not found " + item.getIdentifier());
                        }
                    }

                    logger.info("Updating item " + item.getIdentifier() + " in landscape " + input.getIdentifier());

                    ItemFactory.assignAll((Item) item, description);
                    inLandscape.add((Item) item);
                }
        );

        landscape.setItems(inLandscape);
        deleteUnreferenced(input, inLandscape, existingItems, logger)
                .forEach(item -> landscape.getItems().remove(item));
    }

    private void fillGroups(LandscapeDescription input, LandscapeImpl landscape) {

        //todo check if this can run in GroupResolver
        landscape.getItems().forEach(item -> {
            landscape.getGroup(item.getGroup()).getItems().add(item);
        });

        input.getGroups().forEach((s, groupItem) -> {
            GroupDescription description = (GroupDescription) groupItem;
            Group group = (Group) landscape.getGroups().get(description.getIdentifier());
            description.getContains().forEach(condition -> {
                group.getItems().addAll(Items.query(condition, List.copyOf(landscape.getItems())));
            });
        });
    }


    private List<LandscapeItem> deleteUnreferenced(
            final LandscapeDescription landscapeDescription,
            Set<Item> kept,
            Set<Item> all,
            ProcessLog logger
    ) {
        if (landscapeDescription.isPartial()) {
            logger.info("Incremental change, will not remove any unreferenced items.");
            return new ArrayList<>();
        }

        List<LandscapeItem> removed = removed(kept, all);
        logger.info("Removing " + removed.size() + " sources in env " + landscapeDescription.getIdentifier());
        return removed;
    }


    private void linkItems(final LandscapeDescription input, final LandscapeImpl landscape, ProcessLog logger) {

        input.getItemDescriptions().forEach(serviceDescription -> {
            Item origin = (Item) Items.pick(serviceDescription, landscape.getItems());
            if (!input.isPartial()) {
                logger.info("Clearing relations of " + origin);
                origin.getRelations().clear(); //delete all relations on full update
            }
        });

        input.getItemDescriptions().forEach(serviceDescription -> {
            Item origin = (Item) Items.pick(serviceDescription, landscape.getItems());
            serviceDescription.getRelations().forEach(relationDescription -> {

                var fqiSource = FullyQualifiedIdentifier.from(relationDescription.getSource());
                var fqiTarget = FullyQualifiedIdentifier.from(relationDescription.getTarget());
                Item source = (Item) Items.find(fqiSource, landscape.getItems()).orElse(null);
                if (source == null) {
                    logger.warn("Relation source " + relationDescription.getSource() + " not found");
                    return;
                }
                Item target = (Item) Items.find(fqiTarget, landscape.getItems()).orElse(null);

                if (target == null) {
                    logger.warn("Relation target " + relationDescription.getTarget() + " not found");
                    return;
                }

                Iterator<RelationItem<Item>> iterator = origin.getRelations().iterator();
                Relation existing = null;
                Relation created = new Relation(source, target);
                while (iterator.hasNext()) {
                    existing = (Relation) iterator.next();
                    if (existing.equals(created)) {
                        logger.info(String.format("Updating relation between %s and %s", existing.getSource(), existing.getTarget()));
                        existing.setDescription(relationDescription.getDescription());
                        existing.setFormat(relationDescription.getFormat());
                        break;
                    }
                    existing = null; //no break: no hit, will be created below
                }

                if (existing == null) {
                    created.setDescription(relationDescription.getDescription());
                    created.setFormat(relationDescription.getFormat());
                    created.setType(relationDescription.getType());

                    origin.getRelations().add(created);
                    if (source == origin)
                        target.getRelations().add(created);
                    else
                        source.getRelations().add(created);

                    logger.info(
                            String.format("Adding relation %s between %s and %s", created.getType(), created.getSource(), created.getTarget())
                    );
                }
            });
        });
    }

    /**
     * Returns all elements kept in the second list.
     */
    static List<LandscapeItem> kept(Collection<? extends LandscapeItem> items1, Collection<? extends LandscapeItem> items2) {
        return items2.stream().filter(item -> exists(item, items1)).collect(Collectors.toList());
    }

    /**
     * Returns all elements removed from the second list.
     */
    static List<LandscapeItem> removed(Collection<? extends LandscapeItem> items1, Collection<? extends LandscapeItem> items2) {
        return items2.stream().filter(item -> !exists(item, items1)).collect(Collectors.toList());
    }

    /**
     * Returns all elements which are not in the second list
     */
    static List<LandscapeItem> added(Collection<? extends LandscapeItem> items1, Collection<? extends LandscapeItem> existing) {
        return items1.stream()
                .filter(item -> !exists(item, existing))
                .collect(Collectors.toList());
    }

    private static boolean exists(LandscapeItem item, Collection<? extends LandscapeItem> items) {
        return items.stream().anyMatch(
                inList -> item.getFullyQualifiedIdentifier().isSimilarTo(inList)
        );
    }

}