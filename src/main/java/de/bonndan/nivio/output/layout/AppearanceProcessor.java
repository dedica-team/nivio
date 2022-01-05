package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.util.URLFactory;
import de.bonndan.nivio.output.icons.LocalIcons;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Resolves color and icons for {@link de.bonndan.nivio.model.Component}
 */
@Service
public class AppearanceProcessor {

    private final IconService iconService;

    public AppearanceProcessor(IconService iconService) {
        this.iconService = iconService;
    }

    public void process(@NonNull final Landscape landscape) {
        Objects.requireNonNull(landscape).getGroupItems().forEach(group -> {
            setIconAndFillAppearance(group);
            landscape.getItems().retrieve(group.getItems()).forEach(this::setIconAndFillAppearance);
        });
        setIconAndFillAppearance(landscape);
    }

    private void setIconAndFillAppearance(Labeled labeled) {

        if (labeled instanceof Item) {
            labeled.setLabel(Label._icondata, iconService.getIconUrl((Item) labeled));
        } else {
            String icon = labeled.getLabel(Label.icon);
            if (StringUtils.hasLength(icon)) {
                URLFactory.getURL(icon)
                        .flatMap(iconService::getExternalUrl)
                        .ifPresent(s -> labeled.setLabel(Label._icondata, s));
            }
            if (!StringUtils.hasLength(icon) && labeled instanceof Group) {
                LocalIcons localIcons = new LocalIcons();
                labeled.setLabel(Label._icondata, localIcons.getDefaultGroupIcon());
            }
        }

        String fill = labeled.getLabel(Label.fill);
        if (StringUtils.hasLength(fill)) {
            URLFactory.getURL(fill)
                    .flatMap(iconService::getExternalUrl)
                    .ifPresent(s -> labeled.setLabel(Label._filldata, s));
        }
    }
}