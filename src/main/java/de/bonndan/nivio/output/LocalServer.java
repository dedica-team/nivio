package de.bonndan.nivio.output;

import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.output.icons.Icons;
import de.bonndan.nivio.output.icons.VendorIcons;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import static de.bonndan.nivio.output.icons.Icons.DEFAULT_ICON;

@Component
public class LocalServer implements EnvironmentAware {

    private static Environment env;
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalServer.class);
    public static final String VENDORICONS_PATH = "/vendoricons";
    public static final String VENDOR_PREFIX = "vendor://";

    private final VendorIcons vendorIcons;
    private final URL defaultIcon;

    private String imageProxy;

    /**
     * without slash
     */
    private final String baseUrl;

    public LocalServer(@Value("${nivio.baseUrl:}") String baseUrl, VendorIcons vendorIcons) {
        if (!StringUtils.isEmpty(baseUrl)) {
            this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        } else {
            this.baseUrl = "http://" + host() + ":" + port();
        }
        this.vendorIcons = vendorIcons;
        defaultIcon = getUrl(DEFAULT_ICON.getName());
    }

    /**
     * Returns the current publically visible url.
     *
     * @param path path to add
     * @return url with host, port
     */
    public URL getUrl(String path) {
        try {
            return new URL(baseUrl + (path.startsWith("/") ? path : "/" + path));
        } catch (MalformedURLException ignored) {
            LOGGER.warn("Failed to build url for {}", path);
            return defaultIcon;
        }
    }

    public URL getIconUrl(Labeled item) {

        String icon = item.getLabel(Label.icon);
        if (!StringUtils.isEmpty(icon)) {

            if (icon.startsWith(VENDOR_PREFIX)) {
                String key = icon.replace(VENDOR_PREFIX, "").toLowerCase();
                return vendorIcons.getUrl(key).map(url -> proxiedUrl(url)).orElse(defaultIcon);
            }

            URL iconUrl = getIconUrl(icon);
            return iconUrl != null ? iconUrl : getUrl(icon);
        }

        String type = item.getLabel(Label.type);
        if (StringUtils.isEmpty(type)) {
            return getIconUrl(DEFAULT_ICON.getName());
        }

        //fallback to item.type
        Icons ic = Icons.of(type.toLowerCase()).orElse(DEFAULT_ICON);
        return getIconUrl(ic.getName());
    }

    /**
     * Provides an URL for a locally served icon.
     */
    private URL getIconUrl(String icon) {
        URL url = URLHelper.getURL(icon);

        //local icon urls are not supported
        if (url != null && URLHelper.isLocal(url)) {
            url = null;
        }

        if (url == null) {
            return getUrl("/icons/" + icon + ".png");
        }

        return proxiedUrl(url);
    }

    private URL proxiedUrl(URL url) {

        if (imageProxy == null){
            URL imageProxy = getUrl(VENDORICONS_PATH);
            setImageProxy(imageProxy.toString());
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

    private static String host() {
        return InetAddress.getLoopbackAddress().getHostName();
    }

    private static String port() {

        if (env != null) {
            String port = env.getProperty("local.server.port");
            if (port != null && Integer.valueOf(port) != 0)
                return port;
        }

        return "8080";
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }
}
