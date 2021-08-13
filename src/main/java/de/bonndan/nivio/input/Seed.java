package de.bonndan.nivio.input;

import de.bonndan.nivio.config.ConfigurableEnvVars;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Evaluation of the SEED environment variable.
 */
public class Seed {

    private static final Logger logger = LoggerFactory.getLogger(Seed.class);

    private final List<URL> seedUrls;
    private final String demo;


    /**
     * @throws RuntimeException to fail early on wrong config
     */
    public Seed(@NonNull final String seed, @Nullable final String demo) {

        this.demo = demo == null ? "" : demo;
        if (Objects.requireNonNull(seed).isEmpty()) {
            this.seedUrls = new ArrayList<>();
            return;
        }
        String[] strings = StringUtils.commaDelimitedListToStringArray(seed);
        this.seedUrls = Arrays.stream(strings)
                .map(this::asURL)
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
        if (!StringUtils.hasLength(demo)) {
            return demoFiles;
        }
        String value = ConfigurableEnvVars.DEMO.value().get();

        Path currentRelativePath = Paths.get("");
        String absPath = currentRelativePath.toAbsolutePath().toString();
        try {
            demoFiles.add(new File(absPath + "/src/test/resources/example/example_k8s.yml").toURI().toURL());
            if (value.equalsIgnoreCase("all")) {
                demoFiles.add(new File(absPath + "/src/test/resources/example/inout.yml").toURI().toURL());
                demoFiles.add(new File(absPath + "/src/test/resources/example/internals.yml").toURI().toURL());
                demoFiles.add(new File(absPath + "/src/test/resources/example/dedica_dot.yml").toURI().toURL());
            }
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
        logger.info("Using seeds: {}", seedUrls);
        return seedUrls;
    }
}
