package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;


/**
 * Resolves all links of all landscape components.
 *
 *
 */
public class LinksResolver extends Resolver {

    private final LinkResolverFactory linkResolverFactory;

    /**
     * @param logger the log belonging to the landscape.
     * @param linkResolverFactory factory responsible to create single link resolvers.
     */
    public LinksResolver(ProcessLog logger, LinkResolverFactory linkResolverFactory) {
        super(logger);
        this.linkResolverFactory = linkResolverFactory;
    }

    @Override
    public void process(LandscapeDescription input, LandscapeImpl landscape) {
        resolve(landscape, landscape);
        landscape.getGroups().forEach((s, groupItem) -> resolve(landscape, groupItem));
        landscape.getItems().all().forEach(item -> resolve(landscape, item));
    }

    private <T extends Linked & Labeled & Component> void resolve(Landscape landscape, T component) {

        component.getLinks().forEach((s, link) -> {
            linkResolverFactory.getResolver(s).resolve(link)
                    .whenComplete((resolver, throwable) -> {
                        if (throwable != null) {
                            processLog.error(ProcessingException.of(landscape, throwable));
                        } else {
                            resolver.applyData(component);
                            processLog.info("Successfully read link " + s + " of " + component.getFullyQualifiedIdentifier().jsonValue());
                        }
                    });
        });
    }


}
