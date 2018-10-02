package de.bonndan.nivio.input.compose2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.HttpService;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.landscape.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DockerComposeFileTest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDescriptionFactory.class);
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private FileFetcher fileFetcher;

    @BeforeEach
    public void setup() {
        fileFetcher = new FileFetcher(new HttpService());
    }

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    @Test
    public void fromYaml() throws IOException {


        SourceReference file = new SourceReference(getRootPath() + "/src/test/resources/example/services/docker-compose.yml");
        String yml = fileFetcher.get(file);
        DockerComposeFile source = mapper.readValue(yml, DockerComposeFile.class);
        assertNotNull(source);
        assertEquals(3, source.services.size());

        ComposeService service = source.services.get("web");
        assertNotNull(service);
        assertNotNull(service.getNetworks());
        service.getNetworks().stream().filter(n -> n.getName().equals("test")).findFirst().orElseThrow();
        service.getNetworks().stream().filter(n -> n.getName().equals("test2")).findFirst().orElseThrow();
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }

}
