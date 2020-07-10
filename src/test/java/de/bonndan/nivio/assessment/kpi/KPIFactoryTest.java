package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.LandscapeConfig;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.model.LandscapeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KPIFactoryTest {

    private ApplicationEventPublisher publisher;
    private KPIFactory kpiFactory;
    private LandscapeImpl landscape;

    @BeforeEach
    public void setup() {
        publisher = mock(ApplicationEventPublisher.class);
        kpiFactory = new KPIFactory(publisher);

        landscape = new LandscapeImpl();
        landscape.setConfig(new LandscapeConfig());
        landscape.setProcessLog(new ProcessLog(mock(Logger.class)));
    }

    @Test
    public void defaultKPIs() {

        Map<String, KPI> configuredKPIs = kpiFactory.getConfiguredKPIs(landscape);
        assertNotNull(configuredKPIs);
        assertEquals(4, configuredKPIs.size());
        assertTrue(configuredKPIs.get(ScalingKPI.IDENTIFIER) instanceof ScalingKPI);
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    public void throwsExceptionOnFailedInit() {

        Map<String, KPIConfig> kpIs = landscape.getConfig().getKPIs();
        KPIConfig config = new KPIConfig();
        config.ranges.put(Status.GREEN.name(), "foobarbaz");
        kpIs.put("foo", config);

        //when
        assertThrows(ProcessingException.class, () -> kpiFactory.getConfiguredKPIs(landscape));
        verify(publisher).publishEvent(any());
    }

    @Test
    public void disabledKPI() {

        //given
        Map<String, KPIConfig> kpIs = landscape.getConfig().getKPIs();
        KPIConfig config = new KPIConfig();
        config.enabled = false;
        kpIs.put("foo", config);

        //when
        Map<String, KPI> configuredKPIs = kpiFactory.getConfiguredKPIs(landscape);

        //then
        KPI kpi = configuredKPIs.get("foo");
        assertNotNull(kpi);
        assertFalse(kpi.isEnabled());
    }
}