package de.bonndan.nivio.input;


import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.util.RootPath;
import de.bonndan.nivio.input.dto.Environment;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EnvironmentFactoryTest {

    @Test
    public void read() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertEquals("Landscape example", environment.getName());
        assertEquals("nivio:example", environment.getIdentifier());
        assertFalse(environment.getSourceReferences().isEmpty());

        SourceReference mapped = environment.getSourceReferences().get(1);
        assertNotNull(mapped);
        assertEquals("nivio", mapped.getFormat());
    }

    @Test
    public void readIncremental() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_incremental_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertEquals("Landscape example", environment.getName());
        assertEquals("nivio:example", environment.getIdentifier());
        assertFalse(environment.getSourceReferences().isEmpty());

        ServiceDescription mapped = environment.getServiceDescription("blog-server");
        assertNotNull(mapped);
        assertEquals("blog1", mapped.getShort_name());
        assertEquals("name2", mapped.getName());
    }

}