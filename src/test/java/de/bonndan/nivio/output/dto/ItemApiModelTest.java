package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

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
        try {
            itemTemplate = getTestItemBuilder("g1", "a").withAddress(new URI("https://dedica.team/"))
                    .withDescription("testDescription")
                    .withOwner("testOwner")
                    .withIcon("testIcon")
                    .withContact("testContact")
                    .withLandscape(landscape).withType("two");
        } catch (URISyntaxException ignored) {
        }
    }

    @Test
    void setsGroupColor() {

        Item s1 = itemTemplate.build();
        group = GroupBuilder.aGroup()
                .withIdentifier("g1")
                .withLandscapeIdentifier(landscape.getIdentifier())
                .withColor("#aabbcc")
                .build();

        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());

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

        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());

        //then
        assertThat(itemApiModel.getColor()).isEqualTo(s1.getColor());
    }

    @Test
    void iconDataAsIcon() {

        Item s1 = itemTemplate.build();
        s1.setLabel(Label._icondata, "foo");
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());

        //then
        assertThat(itemApiModel.getIcon()).isEqualTo(s1.getLabel(Label._icondata));
    }

    @Test
    void getIdentifier() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getIdentifier()).isEqualTo("a");
    }

    @Test
    void getFullyQualifiedIdentifier() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getFullyQualifiedIdentifier()).isEqualTo(FullyQualifiedIdentifier.build("l1", "g1", "a"));
    }

    @Test
    void getName() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getName()).isEqualTo("");
    }

    @Test
    void getOwner() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getOwner()).isEqualTo("testOwner");
    }

    @Test
    void getContact() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getContact()).isEqualTo("testContact");
    }

    @Test
    void getGroup() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getGroup()).isEqualTo("g1");
    }

    @Test
    void getDescription() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getDescription()).isEqualTo("testDescription");
    }

    @Test
    void getJSONRelations() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getJSONRelations()).isEqualTo(Map.of());
    }

    @Test
    void getType() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getType()).isEqualTo("two");
    }

    @Test
    void getAddress() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getAddress()).isEqualTo("https://dedica.team/");
    }

    @Test
    void getInterfaces() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getInterfaces()).isEqualTo(Set.of());
    }

    @Test
    void getTags() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.getTags()).isEqualTo(new String[0]);
    }

    @Test
    void testToString() {
        Item s1 = itemTemplate.build();
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of());
        assertThat(itemApiModel.getColor()).isEqualTo(group.getColor());
        assertThat(itemApiModel.toString()).hasToString("l1/g1/a");
    }

    @Test
    void testKeyMapping() {
        Item s1 = itemTemplate.build();
        s1.setLabel("shortname", "test");
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of("shortname", "short name"));
        assertThat(itemApiModel.getLabels()).containsKey("short name");
    }

    @Test
    void testValueMapping() {
        Item s1 = itemTemplate.build();
        s1.setLabel("lifecycle", "END_OF_LIFE");
        ItemApiModel itemApiModel = new ItemApiModel(s1, group, Map.of("END_OF_LIFE", "end of life"));
        assertThat(itemApiModel.getLabels()).containsValue("end of life");
    }
}