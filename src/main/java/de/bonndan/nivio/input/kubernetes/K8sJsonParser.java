package de.bonndan.nivio.input.kubernetes;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Parses config values for the crossReferenceLabel feature
 */

public class K8sJsonParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(K8sJsonParser.class);
    private static final String FILEPATH = "src/main/resources/k8sLabelConfig.json";
    private static K8sConfig k8sConfig = null;

    private K8sJsonParser() {
    }

    private static K8sConfig getK8sConfig() {
        if (k8sConfig != null) {
            return k8sConfig;
        } else {
            try {
                var testMapper = new ObjectMapper();
                k8sConfig = testMapper.readValue(new File(FILEPATH), K8sConfig.class);
                return k8sConfig;
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
            }
        }
        k8sConfig = new K8sConfig(false, 0, Map.of());
        return k8sConfig;
    }

    public static boolean getExperimentalActive() {
        return getK8sConfig().isActive();
    }

    public static int getMinMatchingLevel() {
        return getK8sConfig().getMinMatchingLabel();
    }

    public static int getExperimentalLevel(Class<? extends ItemAdapter> classToParse) {
        return getExperimentalLevelFromJSON(classToParse.getSimpleName());
    }

    private static int getExperimentalLevelFromJSON(String objectName) {
        return getK8sConfig().getLevel().getOrDefault(objectName, -1);
    }
}
