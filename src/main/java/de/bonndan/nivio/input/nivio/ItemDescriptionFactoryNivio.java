package de.bonndan.nivio.input.nivio;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.ItemDescriptionFactory;
import de.bonndan.nivio.input.ReadingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.util.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ItemDescriptionFactoryNivio implements ItemDescriptionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ItemDescriptionFactoryNivio.class);
    private static final ObjectMapper mapper = Mappers.gracefulYamlMapper;

    private final FileFetcher fetcher;

    public static ItemDescriptionFactory forTesting() {
        return new ItemDescriptionFactoryNivio(new FileFetcher(new HttpService()));
    }

    public ItemDescriptionFactoryNivio(FileFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getFormats() {
        return Arrays.asList("", "nivio");
    }

    @Override
    public List<ItemDescription> getDescriptions(SourceReference reference, URL baseUrl) {

        List<ItemDescription> descriptions = new ArrayList<>();
        String yml = fetcher.get(reference, baseUrl);
        Source source;
        try {
            source = mapper.readValue(yml, Source.class);
        } catch (IOException e) {
            logger.error("Failed to read yml", e);
            throw new ReadingException("Failed to parse yaml service description", e);
        }

        if (source == null) {
            logger.warn("Got null out of yml string " + yml);
            return descriptions;
        }

        if (source.items != null) {
            descriptions.addAll(source.items);
        }

        return descriptions;

    }
}
