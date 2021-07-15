package de.bonndan.nivio.input.kubernetes;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.util.HashMap;

public class K8sJsonParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(K8sJsonParser.class);
    private static final HashMap<String, Integer> cacheMap = new HashMap<>();

    private K8sJsonParser() {
    }

    public static boolean getExperimentalActive() {
        var parser = new JSONParser();
        var active = false;
        try {
            var obj = parser.parse(new FileReader("src/main/app/k8sLabelConfig.json"));
            var jsonObject = (JSONObject) obj;
            var companyList = (JSONObject) jsonObject.get("experimentalFeatureLabel");
            active = Boolean.parseBoolean(companyList.get("active").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return active;
    }

    public static int getExperimentalLevel(Class<? extends Item> classToParse) {
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
            var obj = parser.parse(new FileReader("src/main/app/k8sLabelConfig.json"));
            var jsonObject = (JSONObject) obj;
            var experimentalFeature = (JSONObject) jsonObject.get("experimentalFeatureLabel");
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
