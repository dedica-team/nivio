package de.bonndan.nivio.input;


import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.SourceFormat;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.landscape.LandscapeItems;
import de.bonndan.nivio.landscape.StateProviderConfig;
import de.bonndan.nivio.util.RootPath;
import de.bonndan.nivio.input.dto.Environment;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class EnvironmentFactoryTest {

    @Test
    public void read() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertEquals("Landscape example", environment.getName());
        assertEquals("nivio:example", environment.getIdentifier());
        assertEquals("mail@acme.org", environment.getContact());
        assertFalse(environment.getSourceReferences().isEmpty());

        SourceReference mapped = environment.getSourceReferences().get(1);
        assertNotNull(mapped);
        assertEquals(SourceFormat.NIVIO, mapped.getFormat());
    }

    @Test
    public void readIncremental() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_incremental_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertEquals("Incremental update example", environment.getName());
        assertEquals("nivio:incremental", environment.getIdentifier());
        assertFalse(environment.getSourceReferences().isEmpty());

        ServiceDescription mapped = (ServiceDescription) LandscapeItems.pick("blog-server", null, environment.getServiceDescriptions());
        assertNotNull(mapped);
        assertEquals("blog1", mapped.getShort_name());
        assertEquals("name2", mapped.getName());
    }

    @Test
    public void readStateProviders() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_providers.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertFalse(environment.getStateProviders().isEmpty());

        StateProviderConfig cfg = environment.getStateProviders().get(0);
        assertNotNull(cfg);
        assertEquals("prometheus-exporter", cfg.getType());
        assertTrue(cfg.getTarget().contains("example/rancher_prometheus_exporter.txt"));
    }

    @Test
    public void brokenUrl() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_broken.yml");
        assertThrows(ReadingException.class,() -> EnvironmentFactory.fromYaml(file));

    }
}