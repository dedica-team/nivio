package de.bonndan.nivio.observation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

public class ObserverJsonParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverJsonParser.class);
    private static final String FILEPATH = "src/main/resources/observerDelayConfig.json";
    private static final int DEFAULT_DELAY = 30;
    private static ObserverConfig observerConfig;

    static {
        observerConfig = getObserverConfig();
    }

    private ObserverJsonParser() {
    }

    private static ObserverConfig getObserverConfig() {
        if (observerConfig != null) {
            return observerConfig;
        } else {
            try {
                var testMapper = new ObjectMapper();
                observerConfig = testMapper.readValue(new File(FILEPATH), ObserverConfig.class);
                return observerConfig;
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
            }
        }
        observerConfig = new ObserverConfig(Map.of());
        return observerConfig;
    }

    public static int getDelayFromJson(Class<? extends InputFormatObserver> classToParse) {
        return getObserverConfig().getDelay().getOrDefault(classToParse.getSimpleName(), DEFAULT_DELAY);
    }
}



