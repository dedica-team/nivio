package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.util.URLHelper;
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
        Objects.requireNonNull(landscape).getGroupItems().forEach(group -> landscape.getItems().retrieve(group.getItems()).forEach(this::setItemAppearance));
        setLandscapeAppearance(Objects.requireNonNull(landscape));
        Objects.requireNonNull(landscape).getGroups().forEach((s, group) -> setGroupAppearance(group));
    }

    private void setItemAppearance(Item item) {

        item.setLabel(Label._icondata, iconService.getIconUrl(item));

        String fill = item.getLabel(Label.fill);
        if (StringUtils.hasLength(fill)) {
            URLHelper.getURL(fill)
                    .flatMap(iconService::getExternalUrl)
                    .ifPresent(s -> item.setLabel(Label._filldata, s));
        }

    }

    private void setLandscapeAppearance(Landscape landscape) {

        String icon = landscape.getLabel(Label.icon);
        if (StringUtils.hasLength(icon)) {
            URLHelper.getURL(icon)
                    .flatMap(iconService::getExternalUrl)
                    .ifPresent(s -> landscape.setLabel(Label._icondata, s));
        }


        String fill = landscape.getLabel(Label.fill);
        if (StringUtils.hasLength(fill)) {
            URLHelper.getURL(fill)
                    .flatMap(iconService::getExternalUrl)
                    .ifPresent(s -> landscape.setLabel(Label._filldata, s));
        }


    }

    private void setGroupAppearance(Group group) {

        String icon = group.getLabel(Label.icon);
        if (StringUtils.hasLength(icon)) {
            URLHelper.getURL(icon)
                    .flatMap(iconService::getExternalUrl)
                    .ifPresent(s -> group.setLabel(Label._icondata, s));
        }


        String fill = group.getLabel(Label.fill);
        if (StringUtils.hasLength(fill)) {
            URLHelper.getURL(fill)
                    .flatMap(iconService::getExternalUrl)
                    .ifPresent(s -> group.setLabel(Label._filldata, s));
        }
    }

}