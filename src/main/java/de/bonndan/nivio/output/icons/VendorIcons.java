package de.bonndan.nivio.output.icons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ConfigurationProperties("vendoricons")
@Component
public class VendorIcons {

    private static final Logger LOGGER = LoggerFactory.getLogger(VendorIcons.class);

    private final Map<String, String> vendoricons = new HashMap<>();

    /**
     * Returns the original url of a vendor product icon.
     *
     * @param vendor key in the config
     * @return url
     */
    public Optional<URL> getUrl(String vendor) {

        if (StringUtils.isEmpty(vendor) || !vendoricons.containsKey(vendor.toLowerCase())) {
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
