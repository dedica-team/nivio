package de.bonndan.nivio.input;


import de.bonndan.nivio.util.RootPath;
import de.bonndan.nivio.input.dto.Environment;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EnvironmentFactoryTest {

    @Test
    public void read() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertEquals("Landscape example", environment.getName());
        assertEquals("nivio:example", environment.getIdentifier());
        assertFalse(environment.getSources().isEmpty());
    }

}