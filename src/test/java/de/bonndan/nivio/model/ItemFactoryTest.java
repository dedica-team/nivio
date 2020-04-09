package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ItemFactoryTest {

    private ItemDescription landscapeItem;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        landscapeItem = new ItemDescription();
        landscapeItem.setName("test");
        landscapeItem.setLabel(Label.SHORTNAME, "t");
        landscapeItem.setType("loadbalancer");
        landscapeItem.setLabel(Label.LAYER, LandscapeItem.LAYER_INFRASTRUCTURE);
        landscapeItem.setIdentifier("id");
        landscapeItem.getLinks().put("homepage", new URL("http://home.page"));
        landscapeItem.getLinks().put("repo", new URL("https://acme.git/repo1"));
        landscapeItem.setContact("contact");
        landscapeItem.setLabel(Label.NOTE, "a note");
        landscapeItem.setOwner("Mr. T");
        landscapeItem.setLabel(Label.SOFTWARE, "ABC");
        landscapeItem.setLabel(Label.VERSION, "1");
        landscapeItem.setLabel(Label.TEAM, "A-Team");
        landscapeItem.setLabel(Label.VISIBILITY, "public");
        landscapeItem.setPrefixed(Tagged.LABEL_PREFIX_TAG, new String[]{"a", "b"});
        landscapeItem.setLabel(Label.COSTS, "10000");
        landscapeItem.setLabel(Label.BUSINESS_CAPABILITY, "billing");
    }

    @Test
    public void testCreate() {
        LandscapeImpl l = new LandscapeImpl();
        l.setName("testLandscape");

        Item created = ItemFactory.fromDescription(landscapeItem, l);
        assertNotNull(created);
        assertEquals(l, created.getLandscape());

        assertEquals(landscapeItem.getName(), created.getName());
        assertEquals(landscapeItem.getLabel(Label.SHORTNAME), created.getLabel(Label.SHORTNAME));
        assertEquals(landscapeItem.getType(), created.getType());
        assertEquals(landscapeItem.getOwner(), created.getOwner());
        assertEquals(landscapeItem.getLinks(), created.getLinks());
        assertEquals(landscapeItem.getLabels(Tagged.LABEL_PREFIX_TAG).size(), created.getTags().length);
        assertEquals(landscapeItem.getContact(), created.getContact());
        assertEquals(landscapeItem.getLabel(Label.NOTE), created.getLabel(Label.NOTE));
        assertEquals(landscapeItem.getLabel(Label.TEAM), created.getLabel(Label.TEAM));
        assertEquals(landscapeItem.getLabel(Label.SOFTWARE), created.getLabel(Label.SOFTWARE));
        assertEquals(landscapeItem.getLabel(Label.VERSION), created.getLabel(Label.VERSION));
        assertEquals(landscapeItem.getLabel(Label.VISIBILITY), created.getLabel(Label.VISIBILITY));
        assertEquals(landscapeItem.getInterfaces(), created.getInterfaces());
        assertEquals(landscapeItem.getRelations().size(), created.getRelations().size());
        assertEquals(landscapeItem.getLabels(Label.PREFIX_NETWORK), created.getLabels(Label.PREFIX_NETWORK));
        assertEquals(landscapeItem.getLabel(Label.COSTS), created.getLabel(Label.COSTS));
        assertEquals(landscapeItem.getLabel(Label.BUSINESS_CAPABILITY), created.getLabel(Label.BUSINESS_CAPABILITY));
        assertEquals(landscapeItem.getLifecycle(), created.getLifecycle());

    }
}
