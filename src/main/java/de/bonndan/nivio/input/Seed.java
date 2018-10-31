package de.bonndan.nivio.input;

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
import java.util.List;

public class Seed {

    private static final Logger logger = LoggerFactory.getLogger(Seed.class);

    static final String NIVIO_ENV_DIRECTORY = "file:/opt/nivio/environments";

    public static File getDemoFile() throws MalformedURLException {
        Path currentRelativePath = Paths.get("");
        return new File(
                 currentRelativePath.toAbsolutePath().toString() + "/src/test/resources/example/example_env.yml"
        );
    }

    @Value("${SEED:}")
    private String seed;

    public Seed() {

    }

    public Seed(String seed) {
        this.seed = seed;
    }

    public String getSeed() {
        return seed;
    }

    public List<URL> getLocations() throws MalformedURLException {

        if (StringUtils.isEmpty(seed)) {
            URL url = new URL(NIVIO_ENV_DIRECTORY);
            logger.info("Using default directory " + NIVIO_ENV_DIRECTORY + " as seed.");
            return List.of(url);
        }

        String[] strings = StringUtils.commaDelimitedListToStringArray(seed);
        List<URL> list = new ArrayList<>();
        for (String s : strings) {
            list.add(toURL(s));
        }
        return list;
    }

    private URL toURL(String s) throws MalformedURLException {
        if (!s.contains(":/")) {
            s = "file://" + s;
        }

        return new URL(s);
    }
}
