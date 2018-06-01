package de.bonndan.nivio.input;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EnvironmentFactoryTest {

    @Test
    public void read() {

        File file = new File(getRootPath() + "/src/test/resources/example/example_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertEquals("Environment example", environment.getName());
        assertEquals("nivio:example", environment.getIdentifier());
        assertFalse(environment.getSources().isEmpty());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}