package de.bonndan.nivio.input.rancher1;

import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.observation.InputFormatObserver;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class InputFormatHandlerRancher1 implements InputFormatHandler {

    private static final Logger logger = LoggerFactory.getLogger(InputFormatHandlerRancher1.class);

    @Override
    public List<String> getFormats() {
        return Arrays.asList("rancher1-prometheus");
    }

    @Override
    public List<ItemDescription> getDescriptions(SourceReference reference, URL baseUrl) {

        String landscape = reference.getLandscapeDescription().getIdentifier();

        String combine = URLHelper.combine(baseUrl, reference.getUrl());
        try {
            URL url = new URL(combine);
            PrometheusExporter prometheusExporter = new PrometheusExporter(landscape, url);
            return prometheusExporter.getDescriptions();
        } catch (MalformedURLException e) {
            logger.error("Could not work on prometheus url {}", combine);
            return new ArrayList<>();
        }
    }

    @Override
    public InputFormatObserver getObserver(InputFormatObserver inner, SourceReference sourceReference) {
        //TODO add observer
        return null;
    }
}
