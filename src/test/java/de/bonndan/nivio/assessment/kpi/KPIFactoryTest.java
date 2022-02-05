package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class KPIFactoryTest {

    private KPIFactory kpiFactory;
    private Landscape landscape;
    private LandscapeConfig landscapeConfig;

    @BeforeEach
    public void setup() {
        kpiFactory = new KPIFactory();

        landscapeConfig = new LandscapeConfig();
        landscape = LandscapeFactory.createForTesting("test", "testLandscape")
                .withConfig(landscapeConfig)
                .build();
    }

    @Test
    void loadsBuiltinKPI() {

        //given
        KPIConfig value = new KPIConfig();
        value.enabled = true;
        landscape.getConfig().getKPIs().put(LifecycleKPI.IDENTIFIER, value);

        //when
        Map<String, KPI> configuredKPIs = kpiFactory.getConfiguredKPIs(landscapeConfig.getKPIs());

        //then
        assertThat(configuredKPIs).isNotEmpty().containsKey(Label.lifecycle.name());
        assertThat(configuredKPIs.get(Label.lifecycle.name())).isNotNull().isInstanceOf(LifecycleKPI.class);
        assertThat(configuredKPIs.get(Label.lifecycle.name()).isEnabled()).isTrue();
        assertThat(configuredKPIs.get(Label.lifecycle.name()).getDescription()).isNotEmpty();
    }

    @Test
    void throwsExceptionOnFailedInit() {

        Map<String, KPIConfig> kpIs = landscape.getConfig().getKPIs();
        KPIConfig config = new KPIConfig();
        config.ranges.put(Status.GREEN.name(), "foobarbaz");
        kpIs.put("foo", config);

        //when
        assertThrows(ProcessingException.class, () -> kpiFactory.getConfiguredKPIs(landscapeConfig.getKPIs()));
    }

    @Test
    void disabledKPI() {

        //given
        Map<String, KPIConfig> kpIs = landscape.getConfig().getKPIs();
        KPIConfig config = new KPIConfig();
        config.enabled = false;
        kpIs.put("foo", config);

        //when
        Map<String, KPI> configuredKPIs = kpiFactory.getConfiguredKPIs(landscapeConfig.getKPIs());

        //then
        KPI kpi = configuredKPIs.get("foo");
        assertNotNull(kpi);
        assertFalse(kpi.isEnabled());
    }
}