package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static de.bonndan.nivio.output.icons.IconMapping.DEFAULT_ICON;

@Component
public class LocalIcons {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalIcons.class);

    /**
     * default icon data url
     */
    private String defaultIcon = null;

    /**
     * data url cache
     */
    private final Map<String, String> iconDataUrls = new ConcurrentHashMap<>();

    public LocalIcons() {
        getIconUrl(DEFAULT_ICON.getIcon()).ifPresent(s -> defaultIcon = s);
    }

    /**
     * Provides an URL for a locally served icon.
     *
     * @return an url pointing to a file or a data url
     */
    Optional<String> getIconUrl(String icon) {
        if (StringUtils.isEmpty(icon)) {
            return Optional.empty();
        }

        URL url = URLHelper.getURL(icon).orElse(null);

        //local icon urls are not supported
        if (url != null && URLHelper.isLocal(url)) {
            url = null;
        }

        if (url == null) {
            String iconFile = "/static/icons/svg/" + icon.toLowerCase() + ".svg";
            return asSVGDataUrl(iconFile);
        }

        return Optional.of(url.toString());
    }

    public String getDefaultIcon() {
        return defaultIcon;
    }

    /**
     * Creates a SVG data url from the given resource path. Is cached.
     *
     * @param path local svg icon
     * @return data url
     */
    private Optional<String> asSVGDataUrl(String path) {
        if (iconDataUrls.containsKey(path)) {
            LOGGER.debug("Using cached icon {}", path);
            return Optional.ofNullable(iconDataUrls.get(path));
        }

        Optional<String> dataUrl = DataUrlHelper.asBase64(path).map(s -> DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + s);
        dataUrl.ifPresentOrElse(
                s -> iconDataUrls.put(path, s),
                () -> LOGGER.warn("Failed to load svg icon {}", path)
        );
        return dataUrl;
    }
}
