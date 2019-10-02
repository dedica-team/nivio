package de.bonndan.nivio.stateaggregation;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.StateProviderConfig;
import de.bonndan.nivio.stateaggregation.provider.PrometheusExporter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProviderFactoryTest {

    @Test
    public void testUrlFailure() {
        StateProviderConfig cfg = new StateProviderConfig();
        cfg.setType(StateProviderConfig.TYPE_PROMETHEUS_EXPORTER);
        cfg.setTarget("malformedurl");

        ProviderFactory providerFactory = new ProviderFactory();
        assertThrows(ProcessingException.class,() -> providerFactory.createFor(new LandscapeDescription(), cfg));
    }

    @Test
    public void testCfgFailure() {
        StateProviderConfig cfg = new StateProviderConfig();
        cfg.setType("foobar");
        cfg.setTarget("http://good.url");

        ProviderFactory providerFactory = new ProviderFactory();
        assertThrows(ProcessingException.class,() -> providerFactory.createFor(new LandscapeDescription(), cfg));
    }

    public void testSuccess() {

        StateProviderConfig cfg = new StateProviderConfig();
        cfg.setType(StateProviderConfig.TYPE_PROMETHEUS_EXPORTER);
        cfg.setTarget("http://good.url");

        ProviderFactory providerFactory = new ProviderFactory();
        Provider provider = providerFactory.createFor(new LandscapeDescription(), cfg);
        assertNotNull(provider);
        assertTrue(provider instanceof PrometheusExporter);
    }
}
