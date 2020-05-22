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
import java.util.List;

public class Seed {

    private static final Logger logger = LoggerFactory.getLogger(Seed.class);

    public static final String DEMO = "DEMO";

    static final String NIVIO_ENV_DIRECTORY = "file:/opt/nivio/environments";
    static boolean ESCAPE_AUTHORITY = SystemUtils.IS_OS_WINDOWS;

    public List<URL> getDemoFiles() throws MalformedURLException {
        Path currentRelativePath = Paths.get("");
        String absPath = currentRelativePath.toAbsolutePath().toString();
        List<URL> demoFiles = new ArrayList<>();
        demoFiles.add(new File(absPath + "/src/test/resources/example/example_env.yml").toURI().toURL());
        demoFiles.add(new File(absPath + "/src/test/resources/example/inout.yml").toURI().toURL());
        //demoFiles.add(new File(absPath + "/src/test/resources/example/dedica.yml"));
        return demoFiles;
    }

    @Value("${SEED:}")
    private String seed;

    public Seed() {

    }

    public Seed(String seed) {
        this.seed = seed;
    }

    public boolean hasValue() {
        return !StringUtils.isEmpty(seed);
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
        logger.info("Using seeds: {}", list);
        return list;
    }

    private URL toURL(String s) throws MalformedURLException {

        if (ESCAPE_AUTHORITY && s.matches("^[a-zA-Z]\\:.*")) {
            s = "file:/" + s;
        }

        if (!s.contains(":/")) {
            s = "file://" + s;
        }

        return new URL(s);
    }
}
