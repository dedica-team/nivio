package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.IndexReadAccess;

/**
 * This class resolves all "contains" queries of a group description, i.e. the items are assigned dynamically to a group.
 */
public class GroupQueryResolver extends Resolver {

    protected GroupQueryResolver(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void resolve(LandscapeDescription input) {

        IndexReadAccess<ComponentDescription> indexReadAccess = input.getIndexReadAccess();
        indexReadAccess.all(GroupDescription.class).forEach(group -> {
            // run the query against all landscape items which match the condition
            group.getContains().forEach(condition -> indexReadAccess.search(condition, ItemDescription.class)
                    .forEach(itemDescription -> itemDescription.setGroup(group.getIdentifier())));
        });
    }
}
