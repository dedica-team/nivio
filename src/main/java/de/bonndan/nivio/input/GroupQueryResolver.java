package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Items;
import de.bonndan.nivio.model.LandscapeImpl;

import java.util.List;

/**
 * @todo check if this can run earlier in GroupResolver (perhaps condition would not match items added in between)
 */
public class GroupQueryResolver extends Resolver {

    protected GroupQueryResolver(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void process(LandscapeDescription input, LandscapeImpl landscape) {

        landscape.getItems().stream().forEach(item -> {
            landscape.getGroup(item.getGroup()).getItems().add(item);
        });

        input.getGroups().forEach((s, groupItem) -> {
            GroupDescription description = (GroupDescription) groupItem;
            Group group = (Group) landscape.getGroups().get(description.getIdentifier());
            description.getContains().forEach(condition -> {
                group.getItems().addAll(landscape.getItems().query(condition));
            });
        });
    }
}
