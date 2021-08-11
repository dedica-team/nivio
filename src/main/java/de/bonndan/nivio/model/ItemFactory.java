package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.util.URIHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemFactory {

    private ItemFactory() {
    }

    private static final Logger logger = LoggerFactory.getLogger(ItemFactory.class);

    public static Item getTestItem(String group, String identifier) {
        var landscape = LandscapeBuilder.aLandscape().withIdentifier("test").withName("test").build();
        return getTestItem(group, identifier, landscape);
    }

    public static Item getTestItem(String group, String identifier, Landscape landscape) {
        return new Item(identifier, landscape, group, null,null,null,
                null, null, null, null, null);
    }

    public static ItemBuilder getTestItemBuilder(String group, String identifier) {
        var landscape = LandscapeBuilder.aLandscape().withIdentifier("test").withName("test").build();
        return ItemBuilder.anItem().withGroup(group).withIdentifier(identifier).withLandscape(landscape);
    }

    public static Item fromDescription(@NonNull ItemDescription description, Landscape landscape) {
        Objects.requireNonNull(description, "description is null");

        ItemBuilder builder = ItemBuilder.anItem()
                .withIdentifier(description.getIdentifier())
                .withDescription(description.getDescription())
                .withName(description.getName())
                .withContact(description.getContact())
                .withOwner(description.getOwner())
                .withGroup(description.getGroup())
                .withIcon(description.getIcon())
                .withType(description.getType())
                .withLandscape(landscape);

        if (description.getAddress() != null) {
            builder.withAddress(URI.create(description.getAddress()));
        }

        builder.withInterfaces(description.getInterfaces().stream()
                .map(ServiceInterface::new)
                .collect(Collectors.toSet()));

        builder.withLinks(description.getLinks());
        builder.withLabels(description.getLabels());

        if (!StringUtils.hasLength(builder.getGroup())) {
            builder.withGroup(Group.COMMON);
        }
        return builder.build();
    }

    /**
     * Assigns all values from the description except relations. Description values
     * overwrite all fields except the group
     *
     * @return an updated copy of the original item
     */
    public static Item assignAll(@NonNull final Item item, @Nullable final ItemDescription description) {
        Objects.requireNonNull(item, "Item is null");
        if (description == null) {
            logger.warn("ItemDescription for item {} is null in assignAllValues", item.getIdentifier());
            return item;
        }

        ItemBuilder builder = ItemBuilder.anItem()
                .withIdentifier(item.getIdentifier())
                .withName(item.getName())
                .withDescription(item.getDescription())
                .withContact(item.getContact())
                .withOwner(item.getOwner())
                .withGroup(item.getGroup())
                .withIcon(item.getIcon())
                .withType(item.getType())
                .withLandscape(item.getLandscape())
                .withRelations(item.getRelations())
                .withInterfaces(item.getInterfaces())
                .withLabels(item.getLabels())
                .withLinks(item.getLinks());

        URIHelper.getURI(description.getAddress())
                .ifPresent(builder::withAddress);

        builder.withInterfaces(description.getInterfaces().stream()
                .map(ServiceInterface::new)
                .collect(Collectors.toSet()));

        builder.withName(description.getName());
        builder.withDescription(description.getDescription());
        builder.withOwner(description.getOwner());
        builder.withColor(description.getColor());
        builder.withIcon(description.getIcon());
        builder.withType(description.getType());
        builder.withContact(description.getContact());
        builder.withLabels(description.getLabels());

        return builder.build();
    }
}
