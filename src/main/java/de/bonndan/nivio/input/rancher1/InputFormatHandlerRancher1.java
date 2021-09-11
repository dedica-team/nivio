package de.bonndan.nivio.input.rancher1;

import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.SourceReference;
import de.bonndan.nivio.observation.InputFormatObserver;
import de.bonndan.nivio.util.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

@Service
public class InputFormatHandlerRancher1 implements InputFormatHandler {

    private static final Logger logger = LoggerFactory.getLogger(InputFormatHandlerRancher1.class);

    @Override
    public List<String> getFormats() {
        return List.of("rancher1-prometheus");
    }

    @Override
    public List<LandscapeDescription> applyData(@NonNull final SourceReference reference, @NonNull final LandscapeDescription landscapeDescription) {

        String identifier = reference.getSeedConfig().getIdentifier();
        String combine = URLFactory.combine(reference.getSeedConfig().getBaseUrl(), reference.getUrl().toString());
        try {
            URL url = new URL(combine);
            PrometheusExporter prometheusExporter = new PrometheusExporter(identifier, url);
            landscapeDescription.mergeItems(prometheusExporter.getDescriptions());
        } catch (MalformedURLException e) {
            logger.error("Could not work on prometheus url {}", combine);
        }
        return Collections.singletonList(landscapeDescription);
    }

    @Override
    public InputFormatObserver getObserver(@NonNull InputFormatObserver inner, @NonNull SourceReference sourceReference) {
        //TODO add observer
        return null;
    }
}
