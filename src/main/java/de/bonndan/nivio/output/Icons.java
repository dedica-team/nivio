package de.bonndan.nivio.output;

import de.bonndan.nivio.landscape.ServiceItem;
import de.bonndan.nivio.util.URLHelper;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Icons {

    public static final String DEFAULT_ICON = "service";

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
            DEFAULT_ICON,
            "webservice",
    };

    public static Icon getIcon(ServiceItem service) {

        if (!StringUtils.isEmpty(service.getIcon())) {
            URL vendorUrl = VendorIcons.get(service.getIcon());
            return new Icon(vendorUrl != null ? vendorUrl : getUrl(service.getIcon()), true);
        }

        if (StringUtils.isEmpty(service.getType()))
            return new Icon(getUrl(DEFAULT_ICON));

        //fallback to service
        if (!Arrays.asList(Icons.KNOWN_ICONS).contains(service.getType().toLowerCase()))
            return new Icon(getUrl(DEFAULT_ICON));

        return new Icon(getUrl(service.getType().toLowerCase()));
    }

    /**
     * Provides an URL for a locally served icon.
     */
    private static URL getUrl(String icon) {
        URL url = URLHelper.getURL(icon);

        //local icon urls are not supported
        if (url != null && URLHelper.isLocal(url)) {
            url = null;
        }

        try {
            return url != null ? url : new URL(LocalServer.url("/icons/" + icon + ".png"));
        } catch (MalformedURLException e) {
            try {
                return new URL(LocalServer.url("/icons/" + DEFAULT_ICON + ".png"));
            } catch (MalformedURLException ex) {
                return null;
            }
        }
    }
}
