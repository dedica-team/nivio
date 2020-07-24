package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.LandscapeImpl;
import org.springframework.util.StringUtils;

/**
 * This class resolves all "contains" queries of a group description, i.e. the items are assigned dynamically to a group.
 *
 *
 */
public class GroupQueryResolver extends Resolver {

    protected GroupQueryResolver(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void process(LandscapeDescription input, LandscapeImpl landscape) {

        landscape.getItems().stream().forEach(item -> landscape.getGroup(item.getGroup()).getItems().add(item));

        input.getGroups().forEach((s, groupItem) -> {
            GroupDescription groupDescription = (GroupDescription) groupItem;
            Group group = (Group) landscape.getGroups().get(groupDescription.getIdentifier());
            if (group == null) {
                processLog.warn("Could not resolve group with identifier " + groupDescription.getIdentifier());
                return;
            }
            groupDescription.getContains().forEach(condition -> group.getItems().addAll(landscape.getItems().query(condition)));
        });

        //cleanup to ensure every items has the group identifier
        landscape.getItems().stream().forEach(item -> {
            Group group = landscape.getGroup(item.getGroup()); //if group is empty, COMMON is returned
            item.setGroup(group.getIdentifier());
        });
    }
}
