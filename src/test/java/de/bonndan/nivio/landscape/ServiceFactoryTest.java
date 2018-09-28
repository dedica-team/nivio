package de.bonndan.nivio.landscape;

import de.bonndan.nivio.input.dto.ServiceDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ServiceFactoryTest {

    private ServiceDescription landscapeItem;

    @BeforeEach
    public void setUp() {
        landscapeItem = new ServiceDescription();
        landscapeItem.setName("test");
        landscapeItem.setShort_name("t");
        landscapeItem.setType(LandscapeItem.TYPE_INFRASTRUCTURE);
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
        landscapeItem.setPort(8008);
        landscapeItem.setProtocol("http");
        landscapeItem.setTags(new String[]{"a", "b"});
    }

    @Test
    public void testCreate() {
        Landscape l = new Landscape();
        l.setName("testLandscape");

        Service created = ServiceFactory.fromDescription(landscapeItem, l);
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
        assertEquals(landscapeItem.getPort(), created.getPort());
        assertEquals(landscapeItem.getProtocol(), created.getProtocol());
        assertEquals(landscapeItem.getRepository(), created.getRepository());
        assertEquals(landscapeItem.getNetwork(), created.getNetwork());
    }
}
