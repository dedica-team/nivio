package de.bonndan.nivio.input.dto;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SourceFormat {

    //TODO move to package, use annotations or service loader
    DOCKER_COMPOSE2("docker-compose-v2"),
    KUBERNETES("kubernetes", "k8s"),
    NIVIO("nivio", ""),
    RANCHER1_PROMETHEUS("rancher1-prometheus");

    private final List<String> formats;

    private static final List<SourceFormat> KNOWN_FORMATS;

    static {
        KNOWN_FORMATS = Arrays.asList(NIVIO, DOCKER_COMPOSE2, KUBERNETES, RANCHER1_PROMETHEUS);
    }

    SourceFormat(String format, String... formats) {

        this.formats = new ArrayList<>();
        this.formats.add(format);

        if (formats != null && formats.length > 0) {
            this.formats.addAll(Arrays.asList(formats));
        }
    }

    public static SourceFormat from(String format) {

        if (StringUtils.isEmpty(format))
            return NIVIO;
        format = format.toLowerCase();

        String finalFormat = format;
        return KNOWN_FORMATS.stream()
                .filter(sourceFormat -> sourceFormat.formats.contains(finalFormat))
                .findFirst().orElse(NIVIO);
    }
}
