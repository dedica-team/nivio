package de.bonndan.nivio.output;

import de.bonndan.nivio.api.iconcache.IconsController;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static de.bonndan.nivio.output.Icons.DEFAULT_ICON;

/**
 * Factory for vendor icon urls.
 * <p>
 * Rather than shipping copyright protected trademark logos Nivio uses this
 */
@Service
public class IconService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IconService.class);
    public static final String VENDOR_PREFIX = "vendor://";

    private final Map<String, URL> vendorIcons = new HashMap<>();
    private String imageProxy;

    public IconService() {

        try {
            //http://www.apache.org/foundation/marks/
            vendorIcons.put("apache/httpd", new URL("http://www.apache.org/logos/res/httpd/httpd.png"));
            vendorIcons.put("redhat/keycloak", new URL("https://raw.githubusercontent.com/keycloak/keycloak-misc/master/logo/keycloak_icon_256px.png"));
            vendorIcons.put("k8s", new URL("https://raw.githubusercontent.com/kubernetes/kubernetes/master/logo/logo.png"));
            //https://redis.io/topics/trademark
            vendorIcons.put("redis", new URL("http://download.redis.io/logocontest/82.png"));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void add(String icon, URL url) {
        if (StringUtils.isEmpty(icon))
            return;

        if (!icon.startsWith(VENDOR_PREFIX))
            return;

        String key = icon.replace(VENDOR_PREFIX, "").toLowerCase();
        vendorIcons.put(key, url);
    }

    public URL get(String icon) {
        if (StringUtils.isEmpty(icon))
            return null;

        if (!icon.startsWith(VENDOR_PREFIX))
            return null;

        String key = icon.replace(VENDOR_PREFIX, "").toLowerCase();
        return proxiedUrl(vendorIcons.get(key));
    }

    public Icon getIcon(LandscapeItem service) {

        if (!StringUtils.isEmpty(service.getIcon())) {
            URL vendorUrl = get(service.getIcon());
            return new Icon(vendorUrl != null ? vendorUrl : getUrl(service.getIcon()), true);
        }

        if (StringUtils.isEmpty(service.getType())) {
            return new Icon(getUrl(DEFAULT_ICON.getName()));
        }

        //fallback to service
        Icons icon = Icons.of(service.getType().toLowerCase()).orElse(DEFAULT_ICON);
        return new Icon(getUrl(icon.getName()));
    }

    /**
     * Provides an URL for a locally served icon.
     */
    private URL getUrl(String icon) {
        URL url = URLHelper.getURL(icon);

        //local icon urls are not supported
        if (url != null && URLHelper.isLocal(url)) {
            url = null;
        }

        if (url == null) {
            try {
                return new URL(LocalServer.url("/icons/" + icon + ".png"));
            } catch (MalformedURLException e) {
                LOGGER.warn("Malformed url for icon {}", icon, e);
                return null;
            }
        }

        return proxiedUrl(url);

    }

    private URL proxiedUrl(URL url) {

        if (imageProxy == null){
            String imageProxy = LocalServer.url(IconsController.VENDORICONS_PATH);
            setImageProxy(imageProxy);
        }

        if (!StringUtils.isEmpty(imageProxy)) {
            try {
                return new URL(imageProxy + "//" + url.toString());
            } catch (MalformedURLException e) {
                LOGGER.error("Failed to build image proxy url from {}", imageProxy + "//" + url.getPath());
            }
        }
        return url;
    }

    public void setImageProxy(String imageProxy) {
        this.imageProxy = imageProxy;
    }
}
