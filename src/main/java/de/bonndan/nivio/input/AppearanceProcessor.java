package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.icons.IconService;
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

    @Override
    public ProcessingChangelog process(LandscapeDescription input, Landscape landscape) {

        Optional<String> logo = Optional.ofNullable(landscape.getConfig().getBranding().getMapLogo());
        logo.ifPresent(s -> setLandscapeLogo(landscape, s));

        landscape.getGroupItems().forEach(group -> group.getItems().forEach(item -> setItemAppearance(group, item)));

        return new ProcessingChangelog();
    }

    private void setItemAppearance(Group group, Item item) {

        item.setLabel(Label.icon, iconService.getIconUrl(item));
        String fill = item.getLabel(Label.fill);
        if (!StringUtils.isEmpty(fill)) {
            URLHelper.getURL(fill)
                    .flatMap(iconService::getExternalUrl)
                    .ifPresent(s -> item.setLabel(Label.fill, s));
        }

        if (StringUtils.isEmpty(item.getColor())) {
            item.setLabel(Label.color, group.getColor());
        }
    }

    private void setLandscapeLogo(Landscape landscape, String logo) {
        if (StringUtils.isEmpty(logo)) {
            return;
        }
        URLHelper.getURL(logo)
                .flatMap(iconService::getExternalUrl)
                .ifPresent(s -> landscape.setLabel("logo", s));
    }

}
