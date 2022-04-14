package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.util.URIHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;

public class ItemFactory implements GraphNodeFactory<Item, ItemDescription, Group> {

    public static final ItemFactory INSTANCE = new ItemFactory();

    private ItemFactory() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemFactory.class);

    @Override
    public Item merge(@NonNull final Item existing, @NonNull final Item added) {
        ItemBuilder builder = ItemBuilder.anItem().withParent(existing.getParent());
        if (added.isAttached()) {
            builder.withParent(added.getParent());
        }
        mergeValuesIntoBuilder(existing, added, builder);

        assignSafe(added.getLayer(), builder::withLayer);
        assignSafe(added.getAddress(), s -> builder.withAddress(URI.create(s)));
        assignSafe(added.getInterfaces(), builder::withInterfaces);

        return builder.build();
    }

    @NonNull
    @Override
    public Item createFromDescription(@NonNull final String identifier,
                                      @NonNull final Group parent,
                                      @Nullable final ItemDescription description
    ) {
        Objects.requireNonNull(identifier, "identifier is null");

        ItemBuilder builder = ItemBuilder.anItem()
                .withIdentifier(identifier)
                .withComponentDescription(description)
                .withParent(parent);

        if (description != null) {
            if (description.getAddress() != null) {
                builder.withAddress(URI.create(description.getAddress()));
            }

            if (description.getLayer() != null) {
                builder.withLayer(description.getLayer());
            }

            builder.withInterfaces(description.getInterfaces().stream()
                    .map(ServiceInterface::new)
                    .collect(Collectors.toSet()));
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
            LOGGER.warn("ItemDescription for item {} is null in assignAllValues", item.getIdentifier());
            return item;
        }

        ItemBuilder builder = ItemBuilder.anItem()
                .withIdentifier(item.getIdentifier())
                .withName(item.getName())
                .withDescription(item.getDescription())
                .withContact(item.getContact())
                .withOwner(item.getOwner())
                .withParent(item.getParent())
                .withIcon(item.getIcon())
                .withType(item.getType())
                .withLayer(item.getLayer())
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
        builder.withLayer(description.getLayer());
        builder.withLabels(description.getLabels());

        return builder.build();
    }


    @Deprecated //todo use GraphTestSupport
    public static Item getTestItem(String group, String identifier) {
        return new Item(identifier, null, null, null,
                null, null, null, null, null, null, GroupBuilder.aTestGroup(group).build());
    }

    @Deprecated //todo use GraphTestSupport
    public static ItemBuilder getTestItemBuilder(String group, String identifier) {
        var parent = GroupBuilder.aTestGroup(group).build();
        return ItemBuilder.anItem().withParent(parent).withIdentifier(identifier);
    }
}