package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Landscape;

/**
 * This class resolves all "contains" queries of a group description, i.e. the items are assigned dynamically to a group.
 */
public class GroupQueryProcessor extends Processor {

    protected GroupQueryProcessor(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void process(LandscapeDescription input, Landscape landscape) {
        
        input.getGroups().forEach((s, groupItem) -> {
            Group group = landscape.getGroups().get(groupItem.getIdentifier());
            if (group == null) {
                processLog.warn("Could not resolve group with identifier " + groupItem.getIdentifier());
                return;
            }
            // run the query against all landscape items which match the condition
            groupItem.getContains()
                    .forEach(condition -> landscape.getItems().query(condition).forEach(group::addItem));
        });

        /*
          cleanup to ensure every item has the group identifier: The input DTOs might not have the group reference,
          and all following resolvers might fail to find or set a group. So this is a fallback.
         */
        Group common = landscape.getGroup(Group.COMMON).get();
        landscape.getItems().itemStream()
                .forEach(item -> landscape.getGroup(item.getGroup()).orElse(common).addItem(item));
    }
}
