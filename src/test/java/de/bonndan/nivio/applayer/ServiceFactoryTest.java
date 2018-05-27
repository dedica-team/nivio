package de.bonndan.nivio.applayer;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ServiceFactoryTest {

    @Test
    public void read() throws IOException {

        File file = new File(getRootPath() + "/service.yml");
        Service service = ServiceFactory.fromYaml(file);
        assertEquals("Pivio-Server", service.getName());
    }

    private String getRootPath() {
        ObjectMapper objectMapper = new ObjectMapper();
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}