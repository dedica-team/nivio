package de.bonndan.nivio.input.dot;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.LabelToFieldResolver;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.observation.InputFormatObserver;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import guru.nidi.graphviz.parse.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Reads graphviz dot files to {@link ItemDescription}s.
 */
@Service
public class InputFormatHandlerDot implements InputFormatHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputFormatHandlerDot.class);
    public static final String NIVIO_LABEL_PREFIX = "nivio_";

    private final FileFetcher fileFetcher;

    public InputFormatHandlerDot(FileFetcher fileFetcher) {
        this.fileFetcher = fileFetcher;
    }

    @Override
    public List<String> getFormats() {
        return List.of("dot");
    }

    @Override
    public void applyData(SourceReference reference, URL baseUrl, LandscapeDescription landscapeDescription) {
        List<ItemDescription> itemDescriptions = new ArrayList<>();
        String content = fileFetcher.get(reference, baseUrl);
        List<ItemDescription> items = new ArrayList<>();
        try {
            MutableGraph g = new Parser().read(content);
            g.nodes().forEach(node -> {
                ItemDescription itemDescription = new ItemDescription(node.name().toString());
                node.attrs().forEach(entry -> {
                    if (!entry.getKey().startsWith(NIVIO_LABEL_PREFIX)) {
                        return;
                    }
                    final String key = LabelToFieldResolver.NIVIO_LABEL_PREFIX + entry.getKey().substring(NIVIO_LABEL_PREFIX.length());
                    itemDescription.setLabel(key, entry.getValue());
                });

                node.links().forEach(link -> {
                    if (link.from() == null) {
                        return;
                    }
                    RelationDescription rel = new RelationDescription(link.from().name().toString(), link.to().name().toString());
                    link.attrs().forEach(entry -> {
                        if (!entry.getKey().startsWith(NIVIO_LABEL_PREFIX)) {
                            return;
                        }
                        final String key = entry.getKey().substring(NIVIO_LABEL_PREFIX.length()).toLowerCase(Locale.ROOT);
                        switch (key) {
                            case "format":
                                rel.setFormat((String) entry.getValue());
                                break;
                            case "description":
                                rel.setDescription((String) entry.getValue());
                                break;
                            case "type":
                                rel.setType(RelationType.from((String) entry.getValue()));
                                break;
                        }
                    });
                    itemDescription.addRelation(rel);
                });

                items.add(itemDescription);
            });
            landscapeDescription.mergeItems(items);

        } catch (IOException e) {
            LOGGER.error("Failed to read {}", reference, e);
            return;
        } catch (ParserException e) {
            throw new ProcessingException("Failed to parse dot input file from " + reference, e);
        }
        landscapeDescription.mergeItems(itemDescriptions);
    }

    @Override
    @Nullable
    public InputFormatObserver getObserver(@NonNull final InputFormatObserver inner, @NonNull final SourceReference sourceReference) {
        return inner;
    }

}
