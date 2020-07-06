package de.bonndan.nivio.input;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Evaluation of the SEED environment variable.
 */
public class Seed {

    public static final String DEMO = "DEMO";
    private static final Logger logger = LoggerFactory.getLogger(Seed.class);
    @Value("${SEED:}")
    private String seed;

    public Seed() {

    }

    public Seed(String seed) {
        this.seed = seed;
    }

    public List<URL> getDemoFiles() {
        Path currentRelativePath = Paths.get("");
        String absPath = currentRelativePath.toAbsolutePath().toString();
        List<URL> demoFiles = new ArrayList<>();
        try {
            demoFiles.add(new File(absPath + "/src/test/resources/example/example_env.yml").toURI().toURL());
            demoFiles.add(new File(absPath + "/src/test/resources/example/inout.yml").toURI().toURL());
            //demoFiles.add(new File(absPath + "/src/test/resources/example/dedica.yml"));
        } catch (MalformedURLException e) {
            logger.error("Error in demo files: " + e.getMessage(), e);
        }
        logger.info("Using demo files: {}", demoFiles);
        return demoFiles;
    }

    public boolean hasValue() {
        return !StringUtils.isEmpty(seed);
    }

    public List<String> getLocations() {
        String[] strings = StringUtils.commaDelimitedListToStringArray(seed);
        List<String> list = new ArrayList<>(Arrays.asList(strings));
        logger.info("Using seeds: {}", list);
        return list;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }
}
