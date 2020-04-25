package de.bonndan.nivio.output.icons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
public class VendorIcons {

    private static final Logger LOGGER = LoggerFactory.getLogger(VendorIcons.class);

    private final Map<String, String> vendoricons = new HashMap<>();

    @PostConstruct
    public void init() {
        vendoricons.put("apache/httpd", "http://www.apache.org/logos/res/httpd/httpd.png");
        vendoricons.put("redhat/keycloak", "https://raw.githubusercontent.com/keycloak/keycloak-misc/master/logo/keycloak_icon_256px.png");
        vendoricons.put("k8s", "https://raw.githubusercontent.com/kubernetes/kubernetes/master/logo/logo.png");
        vendoricons.put("kubernetes", "https://raw.githubusercontent.com/kubernetes/kubernetes/master/logo/logo.png");

        //redis.io/topics/trademark
        vendoricons.put("redis", "http://download.redis.io/logocontest/82.png");
        vendoricons.put("prometheus", "https://raw.githubusercontent.com/prometheus/docs/master/static/prometheus_logo.png");
    }

    /**
     * Returns the original url of a vendor product icon.
     *
     * @param vendor key in the config
     * @return url
     */
    public Optional<URL> getUrl(String vendor) {

        if (StringUtils.isEmpty(vendor) || !vendoricons.containsKey(vendor.toLowerCase())) {
            LOGGER.warn("Unknown vendor icon {}", vendor);
            return Optional.empty();
        }

        try {
            return Optional.of(new URL(vendoricons.get(vendor)));
        } catch (MalformedURLException e) {
            LOGGER.warn("Failed to create vendor icon url for {}", vendor);
        }
        return Optional.empty();
    }

    /**
     * Only used for unit testing.
     */
    public void add(String vendor, String url) {
        vendoricons.put(vendor, url);
    }
}
