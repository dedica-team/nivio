package de.bonndan.nivio.output;

import de.bonndan.nivio.landscape.ServiceItem;
import org.springframework.util.StringUtils;

import java.util.Arrays;

public class Icons {

    public static final String[] KNOWN_ICONS = new String[]{
            "api",
            "cache",
            "database",
            "dataflow",
            "firewall",
            "humanuser",
            "interface",
            "keyvaluestore",
            "loadbalancer",
            "lock",
            "messagequeue",
            "mobileclient",
            "server",
            "service",
            "webservice",
    };

    public static String getIcon(ServiceItem service) {
        if (StringUtils.isEmpty(service.getType()))
            return "service";

        //fallback to service
        if (!Arrays.asList(Icons.KNOWN_ICONS).contains(service.getType().toLowerCase()))
            return "service";

        return service.getType().toLowerCase();
    }

    public static String getUrl(ServiceItem item) {
        return "http://localhost:8080/icons/" + getIcon(item) + ".png";
    }
}
