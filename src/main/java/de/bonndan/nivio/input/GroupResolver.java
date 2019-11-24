package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.GroupItem;
import de.bonndan.nivio.model.Groups;
import de.bonndan.nivio.model.LandscapeImpl;
import org.springframework.util.StringUtils;

/**
 * Resolves the groups.
 */
public class GroupResolver {

    private final ProcessLog log;

    public GroupResolver(ProcessLog log) {
        this.log = log;
    }

    public void process(LandscapeDescription input, LandscapeImpl landscape) {
        input.getGroups().forEach((identifier, groupItem) -> {
            Group g = getGroup(identifier, groupItem);

            log.info("Adding or updating group " + g.getIdentifier());
            landscape.addGroup(g);
        });

        input.getItemDescriptions().forEach(itemDescription -> {

            String group = itemDescription.getGroup();
            if (StringUtils.isEmpty(itemDescription.getGroup())) {
                group = Group.COMMON;
            }

            landscape.getGroups().computeIfAbsent(group, s -> getGroup(s, null));
        });
    }

    private Group getGroup(String identifier, GroupItem groupItem) {
        Group g = new Group(identifier);
        Groups.merge(g, groupItem);
        return g;
    }

}
