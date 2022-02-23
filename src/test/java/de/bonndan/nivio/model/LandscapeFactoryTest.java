package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.assessment.kpi.KPIConfig;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class LandscapeFactoryTest {

    private LandscapeDescription description;

    @BeforeEach
    public void setup() throws MalformedURLException {
        description = new LandscapeDescription("foo", "bar", "baz@mail.com");
        description.setSource(new Source("foo"));
        description.setOwner("baz");
        description.setDescription("Hello, World.");
        description.getLinks().put("home", new Link(new URL("https://dedica.team")));
        description.getLabels().put("one", "two");
    }

    @Test
    void create() {
        Landscape landscape = LandscapeBuilder.aLandscape().withComponentDescription(description).build();
        assertNotNull(landscape);
        assertEquals(description.getIdentifier(), landscape.getIdentifier());
        assertEquals(description.getSource(), landscape.getSource());
    }

    @Test
    void createWithMinIdentifier() {
        Landscape landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();
        assertNotNull(landscape);
    }

    @Test
    void createFromInput() {
        Landscape landscape = LandscapeBuilder.aLandscape().withComponentDescription(description).build();

        assertEquals(description.getContact(), landscape.getContact());
        assertEquals(description.getConfig().getGroupBlacklist(), landscape.getConfig().getGroupBlacklist());
        assertEquals(description.getOwner(), landscape.getOwner());
        assertEquals(description.getDescription(), landscape.getDescription());
        assertEquals(description.getName(), landscape.getName());
        assertEquals(1, landscape.getLabels().size());
        assertEquals("two", landscape.getLabels().get("one"));
        assertEquals(1, landscape.getLinks().size());
        assertEquals("https://dedica.team", landscape.getLinks().get("home").getHref().toString());
    }

    @Test
    void recreateFromExisting() {

        Landscape existing = LandscapeBuilder.aLandscape()
                .withIdentifier(description.getIdentifier())
                .withName("A Test")
                .withContact("foo")
                .withOwner("bar")
                .build();

        //when
        Landscape landscape = LandscapeFactory.recreate(existing.getConfiguredBuilder(), description);

        //then
        assertThat(landscape.getContact()).isEqualTo(description.getContact());
        assertEquals(description.getConfig(), landscape.getConfig());
        assertEquals(description.getOwner(), landscape.getOwner());
        assertEquals(description.getDescription(), landscape.getDescription());
        assertEquals(description.getName(), landscape.getName());
        assertEquals(1, landscape.getLabels().size());
        assertEquals("two", landscape.getLabels().get("one"));
        assertEquals(1, landscape.getLinks().size());
        assertEquals("https://dedica.team", landscape.getLinks().get("home").getHref().toString());
    }

    @Test
    void fromInputAddsKPIs() {

        //given
        Landscape existing = LandscapeBuilder.aLandscape().withIdentifier("test").build();
        description.getConfig().getKPIs().put("foo", new KPIConfig());
        description.getConfig().getKPIs().put("bar", new KPIConfig());

        //when
        Landscape landscape = LandscapeFactory.recreate(existing.getConfiguredBuilder(), description);
        assertThat(landscape.getKpis()).isNotEmpty();
        assertThat(landscape.getKpis()).hasSize(2);
    }

    @Test
    void recreateAddsKPIs() {

        //given
        Landscape existing = LandscapeBuilder.aLandscape()
                .withIdentifier(description.getIdentifier())
                .withName("A Test")
                .withContact("foo")
                .withOwner("bar")
                .build();
        description.getConfig().getKPIs().put("foo", new KPIConfig());

        //when
        Landscape landscape = LandscapeFactory.recreate(existing.getConfiguredBuilder(), description);

        assertThat(landscape.getKpis()).isNotEmpty();
        assertThat(landscape.getKpis()).hasSize(1);
    }

    @Test
    void recreatePartialAddsKPIs() {
        description.setIsPartial(true);
        description.getConfig().getKPIs().put("foo", new KPIConfig());
        description.getConfig().getKPIs().put("bar", new KPIConfig());
        Landscape existing = LandscapeBuilder.aLandscape()
                .withIdentifier(description.getIdentifier())
                .withName("A Test")
                .withContact("foo")
                .withOwner("bar")
                .withKpis(Map.of("foo", mock(KPI.class), "baz", mock(KPI.class)))
                .build();

        //when
        Landscape landscape = LandscapeFactory.recreate(existing.getConfiguredBuilder(), description);

        assertThat(landscape.getKpis()).isNotEmpty();
        assertThat(landscape.getKpis()).hasSize(3);
    }
}