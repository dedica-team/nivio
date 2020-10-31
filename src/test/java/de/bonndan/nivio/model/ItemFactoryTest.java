package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemFactoryTest {

    private ItemDescription landscapeItem;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        landscapeItem = new ItemDescription();
        landscapeItem.setName("test");
        landscapeItem.setLabel(Label.shortname, "t");
        landscapeItem.setType("loadbalancer");
        landscapeItem.setLabel(Label.layer, Item.LAYER_INFRASTRUCTURE);
        landscapeItem.setIdentifier("id");
        landscapeItem.setLink("homepage", new URL("http://home.page"));
        landscapeItem.setLink("repo", new URL("https://acme.git/repo1"));
        landscapeItem.setContact("contact");
        landscapeItem.setLabel(Label.note, "a note");
        landscapeItem.setOwner("Mr. T");
        landscapeItem.setLabel(Label.software, "ABC");
        landscapeItem.setLabel(Label.version, "1");
        landscapeItem.setLabel(Label.team, "A-Team");
        landscapeItem.setLabel(Label.visibility, "public");
        landscapeItem.setPrefixed(Tagged.LABEL_PREFIX_TAG, new String[]{"a", "b"});
        landscapeItem.setLabel(Label.costs, "10000");
        landscapeItem.setLabel(Label.capability, "billing");
    }

    @Test
    public void testCreate() {
        Landscape l = LandscapeFactory.create("testLandscape", "testLandscape");

        Item created = ItemFactory.fromDescription(landscapeItem, l);
        assertNotNull(created);
        assertEquals(l, created.getLandscape());

        assertEquals(landscapeItem.getName(), created.getName());
        assertEquals(landscapeItem.getLabel(Label.shortname), created.getLabel(Label.shortname));
        assertEquals(landscapeItem.getType(), created.getType());
        assertEquals(landscapeItem.getOwner(), created.getOwner());
        assertEquals(landscapeItem.getLinks(), created.getLinks());
        assertEquals(landscapeItem.getLabels(Tagged.LABEL_PREFIX_TAG).size(), created.getTags().length);
        assertEquals(landscapeItem.getContact(), created.getContact());
        assertEquals(landscapeItem.getLabel(Label.note), created.getLabel(Label.note));
        assertEquals(landscapeItem.getLabel(Label.team), created.getLabel(Label.team));
        assertEquals(landscapeItem.getLabel(Label.software), created.getLabel(Label.software));
        assertEquals(landscapeItem.getLabel(Label.version), created.getLabel(Label.version));
        assertEquals(landscapeItem.getLabel(Label.visibility), created.getLabel(Label.visibility));
        assertEquals(landscapeItem.getInterfaces(), created.getInterfaces());
        assertEquals(landscapeItem.getRelations().size(), created.getRelations().size());
        assertEquals(landscapeItem.getLabels(Label.network), created.getLabels(Label.network));
        assertEquals(landscapeItem.getLabel(Label.costs), created.getLabel(Label.costs));
        assertEquals(landscapeItem.getLabel(Label.capability), created.getLabel(Label.capability));
        assertEquals(landscapeItem.getLabel(Label.lifecycle), created.getLabel(Label.lifecycle));
    }
}
