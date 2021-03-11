package de.bonndan.nivio.input.compose2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.observation.InputFormatObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InputFormatHandlerCompose2 implements InputFormatHandler {

    private static final Logger logger = LoggerFactory.getLogger(InputFormatHandler.class);
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    private final FileFetcher fileFetcher;

    public InputFormatHandlerCompose2(FileFetcher fileFetcher) {
        this.fileFetcher = fileFetcher;
    }

    public static InputFormatHandler forTesting() {
        return new InputFormatHandlerCompose2(new FileFetcher(new HttpService()));
    }


    @Override
    public List<String> getFormats() {
        return Collections.singletonList("docker-compose-v2");
    }

    public List<ItemDescription> getDescriptions(SourceReference reference, URL baseUrl) {

        List<ItemDescription> itemDescriptions = new ArrayList<>();
        String yml = fileFetcher.get(reference, baseUrl);
        DockerComposeFile source = null;
        try {
            source = mapper.readValue(yml, DockerComposeFile.class);
        } catch (IOException e) {
            logger.error("Failed to read yml", e);
        }
        if (source == null) {
            logger.warn("Got null out of yml string " + yml);
            return itemDescriptions;
        }

        source.services.forEach((identifier, composeService) -> {
            composeService.setIdentifier(identifier);
            itemDescriptions.add(composeService.getDescription());
        });

        return itemDescriptions;

    }

    @Override
    public InputFormatObserver getObserver(InputFormatObserver inner, SourceReference sourceReference) {
        return inner;
    }

}
