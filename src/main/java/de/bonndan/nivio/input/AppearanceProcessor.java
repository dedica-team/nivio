package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.output.icons.ExternalIcons;
import de.bonndan.nivio.util.URLHelper;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Resolves color and icons for {@link de.bonndan.nivio.model.Component}
 *
 * Appearance must be determined after indexing, because values might be needed in api, too.
 */
public class AppearanceProcessor extends Processor {

    private final IconService iconService;

    public AppearanceProcessor(ProcessLog processLog, IconService iconService) {
        super(processLog);
        this.iconService = iconService;
    }

    public void process(LandscapeDescription input, Landscape landscape) {
        landscape.getGroupItems().forEach(groupItem -> {
            setItemAppearance(groupItem);
            groupItem.getItems().forEach(item -> setItemAppearance(item, groupItem));
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
        item.setIcon(iconService.getIconUrl(item));
        String fill = item.getLabel(Label.fill);
        if (!StringUtils.isEmpty(fill)) {
            URLHelper.getURL(fill)
                    .flatMap(iconService::getFillUrl)
                    .ifPresent(s -> item.setLabel(Label.fill, s));
        }
    }

}
