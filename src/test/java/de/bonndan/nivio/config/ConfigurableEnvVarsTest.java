package de.bonndan.nivio.config;

import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurableEnvVarsTest {


    /**
     * Exports the configurable vars for the docs
     */
    @Test
    public void getNotPresent()  {
        Optional<String> s = ConfigurableEnvVars.GITLAB_PASSWORD.value();
        assertThat(s).isEmpty();
    }

    /**
     * Exports the configurable vars for the docs
     */
    @Test
    public void export() throws IOException {
        List<ConfigurableEnvVars> labelExport = Arrays.asList(ConfigurableEnvVars.values());
        labelExport.sort(Comparator.comparing(Enum::name));


        File rst = new File(RootPath.get() + "/docs/source/env_config.rst");
        String s = labelExport.stream()
                .map(entry -> "* **" + entry.name() + "** " + entry.getDescription())
                .collect(Collectors.joining("\n"));
        Files.writeString(rst.toPath(), s, Charset.defaultCharset());
    }
}