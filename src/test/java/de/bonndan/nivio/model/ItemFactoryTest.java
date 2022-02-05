package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.input.dto.ItemDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemFactoryTest {

    private ItemDescription itemDescription;
    private GraphTestSupport graph;

    @BeforeEach
    public void setUp() throws MalformedURLException {

        graph = new GraphTestSupport();

        itemDescription = new ItemDescription();
        itemDescription.setName("test");
        itemDescription.setLabel(Label.shortname, "t");
        itemDescription.setType("loadbalancer");
        itemDescription.setLayer(Layer.infrastructure.name());
        itemDescription.setIdentifier("id");
        itemDescription.setLink("homepage", new URL("http://home.page"));
        itemDescription.setLink("repo", new URL("https://acme.git/repo1"));
        itemDescription.setContact("contact");
        itemDescription.setLabel(Label.note, "a note");
        itemDescription.setOwner("Mr. T");
        itemDescription.setLabel(Label.software, "ABC");
        itemDescription.setLabel(Label.version, "1");
        itemDescription.setLabel(Label.team, "A-Team");
        itemDescription.setLabel(Label.visibility, "public");
        Arrays.stream(new String[]{"a", "b"}).forEach(s -> itemDescription.setPrefixed(Tagged.LABEL_PREFIX_TAG, s));
        itemDescription.setLabel(Label.costs, "10000");
        itemDescription.setLabel(Label.capability, "billing");
        itemDescription.setAddress("foobar.com");
    }

    @Test
    void testCreate() {

        Item created = ItemFactory.INSTANCE.createFromDescription(itemDescription.getIdentifier(), graph.groupA, itemDescription);
        assertNotNull(created);

        assertEquals(itemDescription.getName(), created.getName());
        assertEquals(itemDescription.getDescription(), created.getDescription());
        assertEquals(itemDescription.getLabel(Label.shortname), created.getLabel(Label.shortname));
        assertEquals(itemDescription.getType(), created.getType());
        assertEquals(itemDescription.getOwner(), created.getOwner());
        assertEquals(itemDescription.getLinks(), created.getLinks());
        assertEquals(itemDescription.getLabels(Tagged.LABEL_PREFIX_TAG).size(), created.getTags().length);
        assertEquals(itemDescription.getContact(), created.getContact());
        assertEquals(itemDescription.getLabel(Label.note), created.getLabel(Label.note));
        assertEquals(itemDescription.getLabel(Label.team), created.getLabel(Label.team));
        assertEquals(itemDescription.getLabel(Label.software), created.getLabel(Label.software));
        assertEquals(itemDescription.getLabel(Label.version), created.getLabel(Label.version));
        assertEquals(itemDescription.getLabel(Label.visibility), created.getLabel(Label.visibility));
        assertEquals(itemDescription.getInterfaces(), created.getInterfaces());
        assertEquals(itemDescription.getLabels(Label.network), created.getLabels(Label.network));
        assertEquals(itemDescription.getLabel(Label.costs), created.getLabel(Label.costs));
        assertEquals(itemDescription.getLabel(Label.capability), created.getLabel(Label.capability));
        assertEquals(itemDescription.getLabel(Label.lifecycle), created.getLabel(Label.lifecycle));
        assertEquals(itemDescription.getLayer(), created.getLayer());
        assertEquals(itemDescription.getAddress(), created.getAddress());
    }

    @Test
    void testAssignAll() {

        //given
        Item existing = graph.itemAA;

        ItemDescription update = new ItemDescription();
        update.setIdentifier(existing.getIdentifier());
        update.setDescription("123");
        update.setLabel(Label.version, "2000");
        update.setLabel("newlabel", "foo");
        update.setType("firewall");
        update.setLayer(Layer.infrastructure.name());

        //when
        Item updated = ItemFactory.assignAll(existing, update);

        //then
        assertThat(updated).isNotNull();
        assertThat(updated.getDescription()).isEqualTo("123");
        assertThat(updated.getType()).isEqualTo("firewall");
        assertThat(updated.getLabel(Label.version)).isEqualTo("2000");
        assertThat(updated.getLabel("newlabel")).isEqualTo("foo");
        assertThat(updated.getLayer()).isEqualTo("infrastructure");
    }
}
