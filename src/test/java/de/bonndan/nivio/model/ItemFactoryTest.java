package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ItemFactoryTest {

    private ItemDescription landscapeItem;

    @BeforeEach
    public void setUp() {
        landscapeItem = new ItemDescription();
        landscapeItem.setName("test");
        landscapeItem.setShort_name("t");
        landscapeItem.setType("loadbalancer");
        landscapeItem.setLayer(LandscapeItem.LAYER_INFRASTRUCTURE);
        landscapeItem.setIdentifier("id");
        landscapeItem.setHomepage("http://home.page");
        landscapeItem.setRepository("https://acme.git/repo1");
        landscapeItem.setContact("contact");
        landscapeItem.setNote("a note");
        landscapeItem.setOwner("Mr. T");
        landscapeItem.setSoftware("ABC");
        landscapeItem.setVersion("1");
        landscapeItem.setTeam("A-Team");
        landscapeItem.setVisibility("public");
        landscapeItem.setTags(new String[]{"a", "b"});
        landscapeItem.setCosts("10000");
        landscapeItem.setCapability("billing");
    }

    @Test
    public void testCreate() {
        LandscapeImpl l = new LandscapeImpl();
        l.setName("testLandscape");

        Item created = ServiceFactory.fromDescription(landscapeItem, l);
        assertNotNull(created);
        assertEquals(l, created.getLandscape());

        assertEquals(landscapeItem.getName(), created.getName());
        assertEquals(landscapeItem.getShort_name(), created.getShort_name());
        assertEquals(landscapeItem.getType(), created.getType());
        assertEquals(landscapeItem.getOwner(), created.getOwner());
        assertEquals(landscapeItem.getHomepage(), created.getHomepage());
        assertEquals(landscapeItem.getTags(), created.getTags());
        assertEquals(landscapeItem.getContact(), created.getContact());
        assertEquals(landscapeItem.getNote(), created.getNote());
        assertEquals(landscapeItem.getTeam(), created.getTeam());
        assertEquals(landscapeItem.getSoftware(), created.getSoftware());
        assertEquals(landscapeItem.getVersion(), created.getVersion());
        assertEquals(landscapeItem.getVisibility(), created.getVisibility());
        assertEquals(landscapeItem.getInterfaces(), created.getInterfaces());
        assertEquals(landscapeItem.getDataFlow(), created.getDataFlow());
        assertEquals(landscapeItem.getRepository(), created.getRepository());
        assertEquals(landscapeItem.getNetworks(), created.getNetworks());
        assertEquals(landscapeItem.getCosts(), created.getCosts());
        assertEquals(landscapeItem.getCapability(), created.getCapability());
        assertEquals(landscapeItem.getLifecycle(), created.getLifecycle());

    }
}
