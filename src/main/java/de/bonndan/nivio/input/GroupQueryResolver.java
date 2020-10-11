package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Landscape;

/**
 * This class resolves all "contains" queries of a group description, i.e. the items are assigned dynamically to a group.
 */
public class GroupQueryResolver extends Resolver {

    protected GroupQueryResolver(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void process(LandscapeDescription input, Landscape landscape) {
        
        input.getGroups().forEach((s, groupItem) -> {
            GroupDescription groupDescription = (GroupDescription) groupItem;
            Group group = (Group) landscape.getGroups().get(groupDescription.getIdentifier());
            if (group == null) {
                processLog.warn("Could not resolve group with identifier " + groupDescription.getIdentifier());
                return;
            }
            // run the query against all landscape items which match the condition
            groupDescription.getContains()
                    .forEach(condition -> landscape.getItems().query(condition).forEach(group::addItem));
        });

        /*
          cleanup to ensure every item has the group identifier: The input DTOs might not have the group reference,
          and all following resolvers might fail to find or set a group. So this is a fallback.
         */
        Group common = landscape.getGroup(Group.COMMON).get();
        landscape.getItems().stream()
                .forEach(item -> landscape.getGroup(item.getGroup()).orElse(common).addItem(item));
    }
}
