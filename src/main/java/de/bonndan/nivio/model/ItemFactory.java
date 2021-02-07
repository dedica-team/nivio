package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.util.URIHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;

public class ItemFactory {

    private static final Logger logger = LoggerFactory.getLogger(ItemFactory.class);

    public static Item fromDescription(ItemDescription item, Landscape landscape) {
        if (item == null) {
            throw new RuntimeException("landscape item is null");
        }

        Item landscapeItemImpl = new Item(item.getGroup(), item.getIdentifier());
        landscapeItemImpl.setLandscape(landscape);
        assignAll(landscapeItemImpl, item);
        return landscapeItemImpl;
    }

    /**
     * Assigns all values from the description except relations. Description values
     * overwrite all fields except the group
     */
    public static void assignAll(Item item, ItemDescription description) {
        if (description == null) {
            logger.warn("ServiceDescription for service " + item.getIdentifier() + " is null in assignAllValues");
            return;
        }
        item.setName(description.getName());
        item.setDescription(description.getDescription());
        item.setOwner(description.getOwner());
        item.setColor(description.getColor());
        item.setIcon(description.getIcon());
        item.setContact(description.getContact());
        URIHelper.getURI(description.getAddress()).ifPresent(item::setAddress);

        item.setInterfaces(description.getInterfaces().stream()
                .map(ServiceInterface::new)
                .collect(Collectors.toSet()));

        item.getLinks().putAll(description.getLinks());

        assignSafe(description.getGroup(), item::setGroup);

        description.getLabels().forEach((key, value) -> {
            if (item.getLabel(key) == null || !StringUtils.isEmpty(value)) {
                item.setLabel(key, value);
            }
        });
    }
}
