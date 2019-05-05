package de.bonndan.nivio.output;

import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for vendor icon urls.
 *
 * Rather than shipping copyright protected trademark logos Nivio uses this
 */
public class VendorIcons {

    public static final String PREFIX = "vendor://";

    private static final Map<String, URL> vendorIcons = new HashMap<>();

    static {
        try {
            //http://www.apache.org/foundation/marks/
            vendorIcons.put("apache/httpd", new URL("http://www.apache.org/logos/res/httpd/httpd.png"));

            //
            vendorIcons.put("redhat/keycloak", new URL("https://raw.githubusercontent.com/keycloak/keycloak-misc/master/logo/keycloak_icon_256px.png"));

            //https://redis.io/topics/trademark
            vendorIcons.put("redis", new URL("http://download.redis.io/logocontest/82.png"));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static URL get(String icon) {
        if (StringUtils.isEmpty(icon))
            return null;

        if (!icon.startsWith(PREFIX))
            return null;

        String key = icon.replace(PREFIX, "").toLowerCase();
        return vendorIcons.get(key);
    }
}
