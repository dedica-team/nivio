package de.bonndan.nivio.output.icons;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Mapping of "known" icons (item.type) to Material Design icons.
 */
public class IconMapping {

    private static final Logger LOGGER = LoggerFactory.getLogger(IconMapping.class);

    public static final String DEFAULT_ICON = "cog";

    public static final String BACKEND = "backend";
    public static final String CACHE = "cache";
    public static final String CONTAINER = "container";
    public static final String DEPLOYMENT = "deployment";
    public static final String POD = "pod";
    public static final String FIREWALL = "firewall";
    public static final String FRONTEND = "frontend";
    public static final String INTERFACE = "interface";
    public static final String KEYVALUESTORE = "keyvaluestore";
    public static final String LOADBALANCER = "loadbalancer";
    public static final String MESSAGEQUEUE = "messagequeue";
    public static final String REPLICASET = "replicaset";
    public static final String SECRET = "secret";
    public static final String STATEFULSET = "statefulset";
    public static final String VOLUME = "volume";

    private final Map<String, String> iconsAndAliases = new HashMap<>();

    public IconMapping() {
        iconsAndAliases.put("ext", "application");
        iconsAndAliases.put("system", "server");
        iconsAndAliases.put("service", "application");
        iconsAndAliases.put("webservice", "application");
        iconsAndAliases.put(CACHE, "flash-circle");
        iconsAndAliases.put(FIREWALL, "wall");
        iconsAndAliases.put(VOLUME, "harddisk");
        iconsAndAliases.put(BACKEND, "application-cog");
        iconsAndAliases.put(SECRET, "application-cog");
        iconsAndAliases.put(LOADBALANCER, "call-split");
        iconsAndAliases.put(STATEFULSET, "harddisk-plus");
        iconsAndAliases.put(REPLICASET, "content-copy");
        iconsAndAliases.put(KEYVALUESTORE, "view-list");
        iconsAndAliases.put(INTERFACE, "connection");
        iconsAndAliases.put(CONTAINER, "inbox");
        iconsAndAliases.put(FRONTEND, "television-guide");
        iconsAndAliases.put(MESSAGEQUEUE, "tray-full");
        iconsAndAliases.put(DEPLOYMENT, "rocket-launch");
        iconsAndAliases.put(POD, "application");

        Path path = Paths.get("src", "main", "resources", "static", "icons", "meta.json");
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            MetaEntry[] metaEntries = objectMapper.readValue(path.toFile(), MetaEntry[].class);
            Arrays.stream(metaEntries).forEach(metaEntry -> {
                iconsAndAliases.put(metaEntry.name, metaEntry.name);
                if (metaEntry.aliases != null) {
                    Arrays.stream(metaEntry.aliases).forEach(s -> iconsAndAliases.put(s, metaEntry.name));
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to read icon list", e);
        }
    }

    public Optional<String> getIcon(@Nullable final String nameOrAlias) {
        return Optional.ofNullable(nameOrAlias)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .flatMap(s -> Optional.ofNullable(iconsAndAliases.get(s)));
    }

    private static class MetaEntry {
        // "ab-testing",
        public String name;
        public String[] aliases;
    }
}
