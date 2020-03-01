package de.bonndan.nivio.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        item.setLayer(description.getLayer() != null ? description.getLayer() : LandscapeItem.LAYER_APPLICATION);
        assignSafe(description.getType(), item::setType);

        item.setNote(description.getNote());
        item.setShort_name(description.getShortName());
        item.setIcon(description.getIcon());
        item.setDescription(description.getDescription());
        item.setTags(description.getTags());
        item.setOwner(description.getOwner());

        item.setSoftware(description.getSoftware());
        item.setVersion(description.getVersion());
        item.setInterfaces(description.getInterfaces().stream()
                .map(ServiceInterface::new)
                .collect(Collectors.toSet()));

        item.getLinks().putAll(description.getLinks());
        item.setContact(description.getContact());
        item.setTeam(description.getTeam());

        item.setVisibility(description.getVisibility());
        item.setLifecycle(description.getLifecycle());
        assignSafe(description.getGroup(), item::setGroup);

        item.setCosts(description.getCosts());
        item.setCapability(description.getCapability());

        if (description.getStatuses() != null)
            description.getStatuses().forEach(statusItem -> {
                try {
                    item.setStatus(statusItem);
                } catch (IllegalArgumentException ex) {
                    logger.warn("Failed to set status", ex);
                }
            });

        item.setHostType(description.getHostType());
        item.setNetworks(description.getNetworks());
        item.setMachine(description.getMachine());
        item.setScale(description.getScale());

        description.getLabels().forEach((s, s2) -> {
            item.getLabels().putIfAbsent(s, s2);
        });
    }
}
