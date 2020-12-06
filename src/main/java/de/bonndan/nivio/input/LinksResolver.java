package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.linked.LinkHandlerFactory;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.Linked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Resolves all links of all landscape components.
 */
public class LinksResolver extends Resolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinksResolver.class);

    private final LinkHandlerFactory linkHandlerFactory;

    /**
     * @param logger             the log belonging to the landscape.
     * @param linkHandlerFactory factory responsible to create single link resolvers.
     */
    public LinksResolver(ProcessLog logger, LinkHandlerFactory linkHandlerFactory) {
        super(logger);
        this.linkHandlerFactory = linkHandlerFactory;
    }

    @Override
    public void process(LandscapeDescription input, Landscape landscape) {
        resolveLinks(input, landscape);
        landscape.getGroups().forEach((s, groupItem) -> {
            resolveLinks(input, groupItem);
            groupItem.getItems().forEach(item -> resolveLinks(input, item));
        });
    }

    private <T extends Linked & Labeled & Component> void resolveLinks(LandscapeDescription input, T component) {

        component.getLinks().forEach((key, link) -> linkHandlerFactory.getResolver(key)
                .ifPresent(handler -> {
                            try {
                                handler.resolveAndApplyData(link, component)
                                        .thenAccept(s -> processLog.info(String.format("Successfully read link %s of %s: %s", key, component, s)));
                            } catch (Exception e) {
                                LOGGER.warn("Link resolving failure {} {}", key, component, e);
                                processLog.warn(String.format("Failed read link %s of %s: %s", key, component, e.getMessage()));
                            }
                        }

                ));
    }


}
