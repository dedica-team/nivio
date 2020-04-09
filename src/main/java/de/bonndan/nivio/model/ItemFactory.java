package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;

public class ItemFactory {

    private static final Logger logger = LoggerFactory.getLogger(ItemFactory.class);

    public static Item fromDescription(LandscapeItem item, LandscapeImpl landscape) {
        if (item == null) {
            throw new RuntimeException("landscape item is null");
        }

        Item landscapeItemImpl = new Item();
        landscapeItemImpl.setLandscape(landscape);
        landscapeItemImpl.setIdentifier(item.getIdentifier());
        assignAll(landscapeItemImpl, item);
        return landscapeItemImpl;
    }

    /**
     * Assigns all values from the description except relations. Description values
     * overwrite all fields except the group
     */
    public static void assignAll(Item item, LandscapeItem description) {
        if (description == null) {
            logger.warn("ServiceDescription for service " + item.getIdentifier() + " is null in assignAllValues");
            return;
        }
        item.setName(description.getName());
        item.setDescription(description.getDescription());
        item.setOwner(description.getOwner());

        item.setInterfaces(description.getInterfaces().stream()
                .map(ServiceInterface::new)
                .collect(Collectors.toSet()));

        item.getLinks().putAll(description.getLinks());
        item.setContact(description.getContact());
        item.setLifecycle(description.getLifecycle());
        assignSafe(description.getGroup(), item::setGroup);

        description.getLabels().forEach((key, value) -> {
            if (item.getLabel(key) == null || !StringUtils.isEmpty(value)) {
                item.setLabel(key, value);
            }
        });

        assignStatusValues(description.getLabels(Label.PREFIX_STATUS), item);
    }

    private static void assignStatusValues(Map<String, String> labels, Item item) {
        Map<String, Map<String, String>> byValue = new HashMap<>();
        labels.forEach((s, labelValue) -> {
            String[] parts= s.replace(Label.PREFIX_STATUS + Label.DELIMITER, "").split(Label.DELIMITER);
            if (parts.length != 2)
                return;
            String key = parts[0];
            String other = parts[1];
            byValue.put(key, Map.of(other, labelValue));
        });
        byValue.forEach((key, stringStringMap) -> {
            StatusValue value = new StatusValue(
                    key,
                    Status.valueOf(stringStringMap.get("status")),
                    stringStringMap.get("message")
            );
            item.setStatusValue(value);
        });
    }
}
