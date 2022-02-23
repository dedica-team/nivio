package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemApiModelTest {

    private Item item;
    private Group group;

    @BeforeEach
    void setUp() {
        var graph = new GraphTestSupport();
        group = graph.groupA;
        item = graph.getTestItemBuilder(group.getIdentifier(), "a")
                .withAddress(URI.create("https://dedica.team/"))
                .withDescription("testDescription")
                .withOwner("testOwner")
                .withIcon("testIcon")
                .withContact("testContact")
                .withType("two")
                .withParent(group)
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(item);
    }

    @Test
    void setsColor() {

        Item s1 = item;
        item.setLabel(Label.color, "#aabbcc");

        ItemApiModel itemApiModel = new ItemApiModel(s1);

        //then
        assertThat(itemApiModel.getColor()).isEqualTo(item.getColor());
    }

    @Test
    void doesNotOverwriteColor() {

        Item s1 = item;
        s1.setLabel(Label.color, "#00000");
        group.setLabel(Label.color, "#aabbcc");

        ItemApiModel itemApiModel = new ItemApiModel(s1);

        //then
        assertThat(itemApiModel.getColor()).isEqualTo(s1.getColor());
    }

    @Test
    void iconDataAsIcon() {

        Item s1 = item;
        s1.setLabel(Label._icondata, "foo");
        ItemApiModel itemApiModel = new ItemApiModel(s1);

        //then
        assertThat(itemApiModel.getIcon()).isEqualTo(s1.getLabel(Label._icondata));
    }

    @Test
    void getIdentifier() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getIdentifier()).isEqualTo("a");
    }

    @Test
    void getFullyQualifiedIdentifier() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getFullyQualifiedIdentifier()).isEqualTo(s1.getFullyQualifiedIdentifier());
    }

    @Test
    void getName() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getName()).isEmpty();
    }

    @Test
    void getOwner() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getOwner()).isEqualTo("testOwner");
    }

    @Test
    void getContact() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getContact()).isEqualTo("testContact");
    }

    @Test
    void getGroup() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getGroup()).isEqualTo(item.getParentIdentifier());
    }

    @Test
    void getDescription() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getDescription()).isEqualTo("testDescription");
    }

    @Test
    void getJSONRelations() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getJSONRelations()).isEqualTo(Map.of());
    }

    @Test
    void getType() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getType()).isEqualTo("two");
    }

    @Test
    void getAddress() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getAddress()).isEqualTo("https://dedica.team/");
    }

    @Test
    void getInterfaces() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getInterfaces()).isEqualTo(Set.of());
    }

    @Test
    void getTags() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getTags()).isEqualTo(new String[0]);
    }

    @Test
    void hasLinks() throws MalformedURLException {
        Item s1 = item;
        s1.setLinks(Map.of("foo", new Link(new URL("http://acme.mcom"))));
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getLinks()).hasSize(1);
        assertThat(itemApiModel.getLinks().get("foo"))
                .isNotNull()
                .satisfies(link -> assertThat(link.getHref().toString()).hasToString("http://acme.mcom"));
    }

    @Test
    void testToString() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.toString()).hasToString(item.toString());
    }

    @Test
    void getNetworks() {
        Item s1 = item;
        ItemApiModel itemApiModel = new ItemApiModel(s1);
        assertThat(itemApiModel.getNetworks()).isEqualTo(new String[0]);
    }
}