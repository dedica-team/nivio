package de.bonndan.nivio.input.rancher1;

import de.bonndan.nivio.input.ItemDescriptionFactory;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ItemDescriptionFactoryRancher1 implements ItemDescriptionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ItemDescriptionFactoryRancher1.class);
    private final URL baseUrl;

    public ItemDescriptionFactoryRancher1(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public List<ItemDescription> getDescriptions(SourceReference reference) {

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
}
