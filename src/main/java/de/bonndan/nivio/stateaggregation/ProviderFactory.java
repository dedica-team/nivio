package de.bonndan.nivio.stateaggregation;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.landscape.LandscapeItem;
import de.bonndan.nivio.landscape.StateProviderConfig;
import de.bonndan.nivio.stateaggregation.provider.PrometheusExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class ProviderFactory {

    private static final Logger logger = LoggerFactory.getLogger(ProviderFactory.class);

    public Provider createFor(LandscapeItem landscape, StateProviderConfig config) {

        if (StateProviderConfig.TYPE_PROMETHEUS_EXPORTER.equals(config.getType())) {
            try {
                return new PrometheusExporter(landscape.getIdentifier(), new URL(config.getTarget()));
            } catch (MalformedURLException e) {
                String msg = "Landscape " + landscape.getIdentifier() + " has malformed provider config for type " + config.getType();
                logger.error(msg, e);
                throw new ProcessingException(landscape, msg, e);
            }
        }

        throw new ProcessingException(landscape, "Could not create state provider from config " + config);
    }
}
