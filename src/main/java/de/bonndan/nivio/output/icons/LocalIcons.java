package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static de.bonndan.nivio.output.icons.IconMapping.DEFAULT_ICON;

/**
 * This component is responsible to resolve icons into urls / data urls.
 *
 *
 */
@Component
public class LocalIcons {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalIcons.class);
    private static final String initErrorMsg = "Default icon could not be loaded from icon set folder %s." +
            " Make sure all npm dependencies are installed (or run mvn package).";
    private static final String DEFAULT_ICONS_FOLDER = "/static/icons/svg/";

    /**
     * default icon data url
     */
    private final String defaultIcon;

    private final String iconFolder;

    /**
     * data url cache
     */
    private final Map<String, String> iconDataUrls = new ConcurrentHashMap<>();

    public LocalIcons(@Value("${nivio.iconFolder:#{null}}") String iconFolder) {
        this.iconFolder = iconFolder != null ? iconFolder : DEFAULT_ICONS_FOLDER;
        defaultIcon = getIconUrl(DEFAULT_ICON.getIcon()).orElseThrow(() -> {
            throw new RuntimeException(String.format(initErrorMsg, this.iconFolder));
        });
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
            String iconFile = String.format("%s%s.svg", iconFolder, icon.toLowerCase());
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
