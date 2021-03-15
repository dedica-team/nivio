package de.bonndan.nivio.input;

import de.bonndan.nivio.config.ConfigurableEnvVars;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Evaluation of the SEED environment variable.
 */
public class Seed {

    private static final Logger logger = LoggerFactory.getLogger(Seed.class);

    private final List<URL> seed;

    /**
     * @param seed comma separated urls
     * @throw RuntimeException to fail early on wrong config
     */
    public Seed(Optional<String> seed) {
        if (seed.isEmpty()) {
            this.seed = new ArrayList<>();
            return;
        }
        String[] strings = StringUtils.commaDelimitedListToStringArray(seed.get());
        this.seed = Arrays.stream(strings)
                .map(s -> asURL(s))
                .collect(Collectors.toList());
    }

    private URL asURL(String s) {
        return URLHelper.getURL(s)
                .or(() -> {
                    try {
                        File file = new File(s);
                        if (file.exists()) {
                            return Optional.of(file.toURI().toURL());
                        }
                    } catch (MalformedURLException ignored) {
                    }
                    return Optional.empty();
                })
                .orElseThrow(() -> new RuntimeException(String.format("SEED part is not a URL: '%s'", s)));

    }

    /**
     * @return the demo file locations
     */
    public List<URL> getDemoFiles() {
        List<URL> demoFiles = new ArrayList<>();
        if (ConfigurableEnvVars.DEMO.value().isEmpty()) {
            return demoFiles;
        }
        Path currentRelativePath = Paths.get("");
        String absPath = currentRelativePath.toAbsolutePath().toString();
        try {
            demoFiles.add(new File(absPath + "/src/test/resources/example/example_env.yml").toURI().toURL());
            demoFiles.add(new File(absPath + "/src/test/resources/example/inout.yml").toURI().toURL());
            demoFiles.add(new File(absPath + "/src/test/resources/example/internals.yml").toURI().toURL());
            demoFiles.add(new File(absPath + "/src/test/resources/example/dedica.yml").toURI().toURL());
        } catch (MalformedURLException e) {
            logger.error("Error in demo files: " + e.getMessage(), e);
        }
        logger.info("Using demo files: {}", demoFiles);
        return demoFiles;
    }

    /**
     * The configured locations (URLs) of configurations (files) to read.
     *
     * @return list of configs
     */
    public List<URL> getLocations() {
        logger.info("Using seeds: {}", seed);
        return seed;
    }
}
