package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.AssessableGroup;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.search.ItemIndex;
import de.bonndan.nivio.util.URLHelper;
import io.swagger.models.apideclaration.Items;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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
//        Objects.requireNonNull(landscape).getGroupItems().forEach(group -> landscape.getItems().retrieve(group.getItems()).forEach(this::setItemAppearance));
//        setLandscapeAppearance(landscape);
//        landscape.getGroups().forEach((s, group) -> setGroupAppearance(group));

        setLandscapeGroupItemAppearance(Objects.requireNonNull(landscape));

    }

//    private void setItemAppearance(Item item) {
//
//        item.setLabel(Label._icondata, iconService.getIconUrl(item));
//
//        String fill = item.getLabel(Label.fill);
//        if (StringUtils.hasLength(fill)) {
//            URLHelper.getURL(fill)
//                    .flatMap(iconService::getExternalUrl)
//                    .ifPresent(s -> item.setLabel(Label._filldata, s));
//        }
//
//    }

    private void setLandscapeGroupItemAppearance(Landscape landscape) {

        String landscapeIcon = landscape.getLabel(Label.icon);
        if (StringUtils.hasLength(landscapeIcon)) {
            URLHelper.getURL(landscapeIcon)
                    .flatMap(iconService::getExternalUrl)
                    .ifPresent(s -> landscape.setLabel(Label._icondata, s));
        }

        String landscapeFill = landscape.getLabel(Label.fill);
        if (StringUtils.hasLength(landscapeFill)) {
            URLHelper.getURL(landscapeFill)
                    .flatMap(iconService::getExternalUrl)
                    .ifPresent(s -> landscape.setLabel(Label._filldata, s));
        }

        List<Group> groupList = new ArrayList<>(Objects.requireNonNull(landscape).getGroupItems());

//        Map<String, Group> map = new HashMap<>(landscape.getGroups());
//
//        for (var entry : map.entrySet()) {
//            System.out.println(entry.getKey() + "/" + entry.getValue());
//        }

        for (Group group : groupList) {
            System.out.println("Group: " + group);
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


//            @NonNull
//            public List<? extends Assessable> getChildren() {
//                return getGroups().values().stream()
//                        .map(group -> new AssessableGroup(group, getItems().retrieve(group.getItems())))
//                        .collect(Collectors.toList());
//            }

        }

        List<Item> itemList = new ArrayList<>();
        for (Group group : groupList) {
            Set<Item> itemSet = landscape.getItems().retrieve(group.getItems());
            for (Item item : itemSet) {
                itemList.add(item);
            }
        }

        for (Item item : itemList) {
            item.setLabel(Label._icondata, iconService.getIconUrl(item));

            String fill = item.getLabel(Label.fill);
            if (StringUtils.hasLength(fill)) {
                URLHelper.getURL(fill)
                        .flatMap(iconService::getExternalUrl)
                        .ifPresent(s -> item.setLabel(Label._filldata, s));
            }

        }

    }
//
//    private void setGroupAppearance(Group group) {
//
//        String icon = group.getLabel(Label.icon);
//        if (StringUtils.hasLength(icon)) {
//            URLHelper.getURL(icon)
//                    .flatMap(iconService::getExternalUrl)
//                    .ifPresent(s -> group.setLabel(Label._icondata, s));
//        }
//
//
//        String fill = group.getLabel(Label.fill);
//        if (StringUtils.hasLength(fill)) {
//            URLHelper.getURL(fill)
//                    .flatMap(iconService::getExternalUrl)
//                    .ifPresent(s -> group.setLabel(Label._filldata, s));
//        }
//    }

}