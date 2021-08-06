package de.bonndan.nivio.input.nivio;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.Mappers;
import de.bonndan.nivio.input.ReadingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Handler for nivio's custom input format (yaml).
 */
@Service
public class InputFormatHandlerNivio implements InputFormatHandler {

    private static final Logger logger = LoggerFactory.getLogger(InputFormatHandlerNivio.class);
    private static final ObjectMapper mapper = Mappers.gracefulYamlMapper;

    private final FileFetcher fileFetcher;

    public InputFormatHandlerNivio(FileFetcher fileFetcher) {
        this.fileFetcher = fileFetcher;
    }

    @Override
    public List<String> getFormats() {
        return Arrays.asList("", "nivio");
    }

    @Override
    public void applyData(@NonNull SourceReference reference, URL baseUrl, LandscapeDescription description) {

        String yml = fileFetcher.get(reference, baseUrl);
        Source source;
        try {
            source = mapper.readValue(yml, Source.class);
        } catch (IOException e) {
            logger.error("Failed to read yml", e);
            throw new ReadingException("Failed to parse yaml service description", e);
        }

        if (source == null) {
            logger.warn("Got null out of yml string {}", yml);
            return;
        }

        description.mergeItems(source.items);
        description.mergeGroups(source.groups);

        if (source.templates != null) {
            source.templates.forEach((s, template) -> description.getTemplates().put(s, template));
        }

    }
}
