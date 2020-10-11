package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.icons.LocalIcons;
import org.springframework.util.StringUtils;

/**
 * Resolves color and icons for {@link de.bonndan.nivio.model.Component}
 *
 * Appearance must be determined after indexing, because values might be needed in api, too.
 */
public class AppearanceResolver extends Resolver {

    private final LocalIcons localIcons;

    public AppearanceResolver(ProcessLog processLog, LocalIcons localIcons) {
        super(processLog);
        this.localIcons = localIcons;
    }

    public void process(LandscapeDescription input, LandscapeImpl landscape) {
        landscape.getGroupItems().forEach(groupItem -> {
            Group g = (Group)groupItem;
            setItemAppearance(g);
            g.getItems().forEach(item -> setItemAppearance(item, g));
        });

    }

    private void setItemAppearance(Group group) {
        if (StringUtils.isEmpty(group.getColor())) {
            group.setColor(Color.getGroupColor(group));
        }
    }

    private void setItemAppearance(Item item, Group group) {
        if (StringUtils.isEmpty(item.getColor())) {
            item.setColor(group.getColor());
        }
        item.setIcon(localIcons.getIconUrl(item));
    }

}
