package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.search.ItemMatcher;

import java.util.Iterator;

/**
 * Creates {@link Relation}s between {@link Item}s.
 */
public class ItemRelationProcessor extends Processor {

    protected ItemRelationProcessor(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void process(LandscapeDescription input, Landscape landscape) {
        input.getItemDescriptions().all().forEach(serviceDescription -> {
            Item origin = landscape.getItems().pick(serviceDescription);
            if (!input.isPartial()) {
                processLog.debug(String.format("Clearing relations of %s", origin));
                origin.getRelations().clear(); //delete all relations on full update
            }
        });

        input.getItemDescriptions().all().forEach(description -> {
            Item origin = landscape.getItems().pick(description);
            description.getRelations().forEach(relationDescription -> {

                var fqiSource = ItemMatcher.forTarget(relationDescription.getSource());
                var fqiTarget = ItemMatcher.forTarget(relationDescription.getTarget());
                Item source = landscape.getItems().find(fqiSource).orElse(null);
                if (source == null) {
                    processLog.warn(String.format("Relation source %s not found", relationDescription.getSource()));
                    return;
                }
                Item target = landscape.getItems().find(fqiTarget).orElse(null);

                if (target == null) {
                    processLog.warn(String.format("Relation target %s not found", relationDescription.getTarget()));
                    return;
                }

                Iterator<Relation> iterator = origin.getRelations().iterator();
                Relation existing = null;
                Relation created = new Relation(source, target);
                while (iterator.hasNext()) {
                    existing = iterator.next();
                    if (existing.equals(created)) {
                        processLog.info(String.format("Updating relation between %s and %s", existing.getSource(), existing.getTarget()));
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

                    processLog.info(
                            String.format("Adding relation %s between %s and %s", created.getType(), created.getSource(), created.getTarget())
                    );
                }
            });
        });
    }
}
