package de.bonndan.nivio.input.csv;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.LabelToFieldResolver;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.observation.InputFormatObserver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class InputFormatHandlerCSV implements InputFormatHandler {

    public static final String IDENTIFIER_KEY = "identifier";

    private final FileFetcher fileFetcher;

    public InputFormatHandlerCSV(FileFetcher fileFetcher) {
        this.fileFetcher = fileFetcher;
    }

    @Override
    public List<String> getFormats() {
        return List.of("csv");
    }

    @Override
    public List<ItemDescription> getDescriptions(SourceReference reference, URL baseUrl) {
        List<ItemDescription> itemDescriptions = new ArrayList<>();
        String content = fileFetcher.get(reference, baseUrl);
        CSVReader reader = getReader(reference, content);

        Map<String, Object> mapping = (Map<String, Object>) reference.getProperty("mapping");
        if (mapping == null) {
            throw new ProcessingException(reference.getLandscapeDescription(), "'mapping' must be present in configuration.");
        }
        if (!mapping.containsKey(IDENTIFIER_KEY)) {
            throw new ProcessingException(reference.getLandscapeDescription(), "'" + IDENTIFIER_KEY + "' must be present in configured mapping.");
        }

        reader.iterator().forEachRemaining(strings -> {
            ItemDescription itemDescription = new ItemDescription();
            mapping.forEach((key, value) -> {
                Integer colNum = 0;
                if (value instanceof String) {
                    colNum = Integer.valueOf((String)value);
                }

                if (value instanceof Integer) {
                    colNum = (Integer) value;
                }


                if (IDENTIFIER_KEY.equals(key)) {
                    itemDescription.setIdentifier(strings[colNum]);
                    return;
                }

                if (colNum >= strings.length) {
                    return;
                }
                //relies on LabelToFieldProcessor running later
                itemDescription.getLabels().put(LabelToFieldResolver.NIVIO_LABEL_PREFIX + key, strings[colNum]);
            });
            itemDescriptions.add(itemDescription);
        });

        return itemDescriptions;
    }

    @Override
    @Nullable
    public InputFormatObserver getObserver(InputFormatObserver inner, SourceReference sourceReference) {
        return inner;
    }

    private CSVReader getReader(SourceReference reference, String content) {
        String separator = (String) Optional.ofNullable(reference.getProperty("separator")).orElse(";");
        int skipLines = (int) Optional.ofNullable(reference.getProperty("skipLines")).orElse(0);

        CSVParser parser = new CSVParserBuilder().withSeparator(separator.charAt(0)).build();
        return new CSVReaderBuilder(new StringReader(content))
                .withCSVParser(parser)
                .withSkipLines(skipLines)
                .build();
    }
}
