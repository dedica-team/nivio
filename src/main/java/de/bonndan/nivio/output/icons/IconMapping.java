package de.bonndan.nivio.output.icons;

import java.util.Arrays;
import java.util.Optional;

/**
 * Mapping of "known" icons (item.type) to Material Design icons.
 *
 *
 */
public enum IconMapping {

    DEFAULT_ICON("cog"),
    BACKEND("application-cog"),
    CACHE("flash-circle"),
    CONTAINER("inbox"),
    DEPLOYMENT("rocket-launch"),
    POD("application"),
    FIREWALL("wall"),
    FRONTEND("television-guide"),
    HUMANUSER("account"),
    INTERFACE("connection"),
    KEYVALUESTORE("view-list"),
    LOADBALANCER("call-split"),
    MESSAGEQUEUE("tray-full"),
    MOBILECLIENT("cellphone"),
    REPLICASET("content-copy"),
    SERVICE("information-outline"),
    SECRET("application-cog"),
    STATEFULSET("harddisk-plus"),
    VOLUME("harddisk"),
    WEBSERVICE("application");

    private final String icon;

    IconMapping(String icon) {
        this.icon = icon;
    }

    public static Optional<IconMapping> of(String serviceType) {
        return Arrays.stream(values())
                .filter(iconMapping -> iconMapping.name().equalsIgnoreCase(serviceType))
                .findFirst();
    }

    public String getIcon() {
        return icon;
    }
}
