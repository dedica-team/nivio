package de.bonndan.nivio.output.icons;

import java.util.Arrays;
import java.util.Optional;

public enum Icons {

    DEFAULT_ICON("cog"),
    API("api"),
    CACHE("cache"),
    CONTAINER("container"),
    DATABASE("database"),
    DATAFLOW("dataflow"),
    FIREWALL("firewall"),
    HUMANUSER("humanuser"),
    INTERFACE("interface"),
    KEYVALUESTORE("keyvaluestore"),
    LOADBALANCER("loadbalancer"),
    LOCK("lock"),
    MESSAGEQUEUE("messagequeue"),
    MOBILECLIENT("mobileclient"),
    POD("pod"),
    SERVER("server"),
    VOLUME("volume"),
    WEBSERVICE("webservice"),
    NIVIO("nivio")
    ;

    private final String name;

    Icons(String name) {
        this.name = name;
    }

    public static Optional<Icons> of(String serviceType) {
        return Arrays.stream(values())
                .filter(icons -> icons.getName().equals(serviceType))
                .findFirst();
    }

    public String getName() {
        return name;
    }
}
