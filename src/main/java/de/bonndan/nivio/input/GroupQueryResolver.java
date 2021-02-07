package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;

/**
 * This class resolves all "contains" queries of a group description, i.e. the items are assigned dynamically to a group.
 */
public class GroupQueryResolver extends Resolver {

    protected GroupQueryResolver(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void resolve(LandscapeDescription input) {
        
        input.getGroupDescriptions().forEach((s, group) -> {
            // run the query against all landscape items which match the condition
            group.getContains().forEach(condition -> input.getItemDescriptions().query(condition)
                    .forEach(itemDescription -> itemDescription.setGroup(group.getIdentifier())));
        });
    }
}
