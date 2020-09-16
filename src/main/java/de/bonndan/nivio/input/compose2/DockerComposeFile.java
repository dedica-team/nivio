package de.bonndan.nivio.input.compose2;


import java.util.HashMap;
import java.util.Map;

/**
 * Represents docker-compose file content with its sections.
 */
public class DockerComposeFile {

    public String version;

    public final Map<String, ComposeService> services = new HashMap<>();
}
