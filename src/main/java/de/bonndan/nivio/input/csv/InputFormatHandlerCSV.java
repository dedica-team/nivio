package de.bonndan.nivio.input.csv;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import de.bonndan.nivio.input.*;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.*;

/**
 * Reads csv files to {@link ItemDescription}s.
 */
@Service
public class InputFormatHandlerCSV implements InputFormatHandler {

    public static final String IDENTIFIER_KEY = "identifier";
    public static final String DELIMITER = ";";

    private final FileFetcher fileFetcher;

    public InputFormatHandlerCSV(FileFetcher fileFetcher) {
        this.fileFetcher = fileFetcher;
    }

    @Override
    public List<String> getFormats() {
        return List.of("csv");
    }

    @Override
    public List<LandscapeDescription> applyData(@NonNull final SourceReference reference, @NonNull final LandscapeDescription landscapeDescription) {

        List<ItemDescription> itemDescriptions = new ArrayList<>();
        String content = fileFetcher.get(reference);
        CSVReader reader = getReader(reference, content);

        Map<String, Object> mapping = (Map<String, Object>) reference.getProperty("mapping");
        if (mapping == null) {
            throw new ProcessingException(reference, "'mapping' must be present in configuration.");
        }
        if (!mapping.containsKey(IDENTIFIER_KEY)) {
            throw new ProcessingException(reference, String.format("'%s' must be present in configured mapping.", IDENTIFIER_KEY));
        }

        reader.iterator().forEachRemaining(strings -> {
            ItemDescription itemDescription = new ItemDescription();
            RelationDescription relationDescription = null;
            Map<String, String> labels = new HashMap<>();
            for (Map.Entry<String, Object> entry : mapping.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                int colNum = getColNum(value, 0);

                String columnValue = strings[colNum];
                if (IDENTIFIER_KEY.equals(key)) {
                    if (columnValue.contains(DELIMITER)) {
                        String[] split = columnValue.split(DELIMITER);
                        itemDescription.setIdentifier(split[0]);
                        relationDescription = new RelationDescription(split[0], split[1]);
                    } else {

                        itemDescription.setIdentifier(columnValue);
                    }
                    continue;
                }
                //relies on LabelToFieldProcessor running later
                labels.put(LabelToFieldResolver.NIVIO_LABEL_PREFIX + key, columnValue);
            }

            if (relationDescription != null) {
                relationDescription.setLabels(labels);
                itemDescription.addOrReplaceRelation(relationDescription);
            } else {
                itemDescription.getLabels().putAll(labels);
            }

            itemDescriptions.add(itemDescription);
        });

        itemDescriptions.forEach(itemDescription -> landscapeDescription.getWriteAccess().addOrReplaceChild(itemDescription));

        return Collections.emptyList();
    }

    private int getColNum(Object value, int colNum) {
        if (value instanceof String) {
            colNum = Integer.parseInt((String) value);
        }

        if (value instanceof Integer) {
            colNum = (Integer) value;
        }
        return colNum;
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
