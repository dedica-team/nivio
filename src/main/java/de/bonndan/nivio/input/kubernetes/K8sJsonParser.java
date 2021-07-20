package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.HashMap;

/**
 * Parses config values for the crossReferenceLabel feature
 */

@Service
public class K8sJsonParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(K8sJsonParser.class);
    private static final HashMap<String, Integer> cacheMap = new HashMap<>();
    private static final String JSONLABEL = "experimentalFeatureLabel";
    private static final String FILEPATH = "src/main/resources/k8sLabelConfig.json";

    private K8sJsonParser() {
    }

    public static boolean getExperimentalActive() {
        var parser = new JSONParser();
        var active = false;
        try {
            var obj = parser.parse(new FileReader(FILEPATH));
            var jsonObject = (JSONObject) obj;
            var companyList = (JSONObject) jsonObject.get(JSONLABEL);
            active = Boolean.parseBoolean(companyList.get("active").toString());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
        return active;
    }

    public static int getMinMatchingLevel() {
        var parser = new JSONParser();
        var minMatchingLabels = 4;
        try {
            var obj = parser.parse(new FileReader(FILEPATH));
            var jsonObject = (JSONObject) obj;
            var companyList = (JSONObject) jsonObject.get(JSONLABEL);
            minMatchingLabels = Integer.parseInt(companyList.get("min_matching_label").toString());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
        return minMatchingLabels;
    }

    public static int getExperimentalLevel(Class<? extends ItemAdapter> classToParse) {
        var objectName = classToParse.getSimpleName();
        if (cacheMap.containsKey(objectName)) {
            return cacheMap.get(objectName);
        } else {
            return getExperimentalLevelFromJSON(objectName);
        }
    }

    private static int getExperimentalLevelFromJSON(String objectName) {
        var parser = new JSONParser();
        try {
            var obj = parser.parse(new FileReader(FILEPATH));
            var jsonObject = (JSONObject) obj;
            var experimentalFeature = (JSONObject) jsonObject.get(JSONLABEL);
            var level = (JSONObject) experimentalFeature.get("level");
            var classLevel = level.get(objectName);
            var finalLevel = Integer.parseInt(String.valueOf(classLevel));
            cacheMap.put(objectName, finalLevel);
            return finalLevel;
        } catch (Exception e) {
            LOGGER.info(String.format("No value for %s in config file found", objectName));
        }
        cacheMap.put(objectName, -1);
        return -1;
    }
}
