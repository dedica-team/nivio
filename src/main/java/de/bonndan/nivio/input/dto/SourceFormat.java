package de.bonndan.nivio.input.dto;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SourceFormat {

    DOCKER_COMPOSE2("docker-compose-v2"),
    KUBERNETES("kubernetes", "k8s"),
    NIVIO("nivio", "");

    private final List<String> formats;

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

        if (DOCKER_COMPOSE2.formats.contains(format)) {
            return DOCKER_COMPOSE2;
        }

        if (NIVIO.formats.contains(format)) {
            return NIVIO;
        }

        if (KUBERNETES.formats.contains(format)) {
            return KUBERNETES;
        }

        return NIVIO;
    }
}
