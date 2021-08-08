package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.assertj.core.api.Assertions.assertThat;

class ItemApiModelTest {

    private Landscape landscape;
    private ItemBuilder itemTemplate;
    private Group group;

    @BeforeEach
    void setUp() {
         landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();
         group = new Group("g1", landscape.getIdentifier());
         itemTemplate = getTestItemBuilder("g1", "a").withLandscape(landscape).withType("two");
    }

    @Test
    void setsGroupColor() {

        Item s1 = itemTemplate.build();
        group = GroupBuilder.aGroup()
                .withIdentifier("g1")
                .withLandscapeIdentifier(landscape.getIdentifier())
                .withColor("#aabbcc")
                .build();

        ItemApiModel itemApiModel = new ItemApiModel(s1, group);

        //then
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
    }

    @Test
    void doesNotOverwriteColor() {

        Item s1 = itemTemplate.withColor("#00000").build();
        group = GroupBuilder.aGroup()
                .withIdentifier("g1")
                .withLandscapeIdentifier(landscape.getIdentifier())
                .withColor("#aabbcc")
                .build();

        ItemApiModel itemApiModel = new ItemApiModel(s1, group);

        //then
        assertThat(itemApiModel.getColor()).isEqualTo(s1.getColor());
    }

    @Test
    void iconDataAsIcon() {

        Item s1 = itemTemplate.build();
        s1.setLabel(Label._icondata, "foo");
        ItemApiModel itemApiModel = new ItemApiModel(s1, group);

        //then
        assertThat(itemApiModel.getIcon()).isEqualTo(s1.getLabel(Label._icondata));
    }
}