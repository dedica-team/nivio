package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.util.IconCannotBeLoadedException;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static de.bonndan.nivio.output.icons.IconMapping.DEFAULT_GROUP_ICON;
import static de.bonndan.nivio.output.icons.IconMapping.DEFAULT_ICON;

/**
 * This component is responsible to resolve icons into urls / data urls.
 */
public class LocalIcons {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalIcons.class);
    private static final String INIT_ERROR_MSG = "Default icon could not be loaded from icon set folder %s." +
            " Make sure all npm dependencies are installed (or run mvn package).";
    public static final String DEFAULT_ICONS_FOLDER = "/static/icons/svg/";

    /**
     * default icon data url
     */
    private final String defaultIcon;

    private final String defaultGroupIcon;

    private final String iconFolder;

    /**
     * data url cache
     */
    private final Map<String, String> iconDataUrls = new ConcurrentHashMap<>();

    /**
     * Bean constructor.
     *
     * @param iconFolder optional dir containing a different icon set
     */
    public LocalIcons(@NonNull final String iconFolder) {
        if (StringUtils.hasLength(Objects.requireNonNull(iconFolder))) {
            this.iconFolder = iconFolder.endsWith("/") || iconFolder.endsWith("\\") ? iconFolder : iconFolder + File.separator;
        } else {
            this.iconFolder = DEFAULT_ICONS_FOLDER;
        }
        defaultIcon = getIconUrl(DEFAULT_ICON).orElseThrow(() -> {
            throw new IconCannotBeLoadedException(String.format(INIT_ERROR_MSG, this.iconFolder));
        });

        defaultGroupIcon = getIconUrl(DEFAULT_GROUP_ICON).orElseThrow(() -> {
            throw new IconCannotBeLoadedException(String.format(INIT_ERROR_MSG, this.iconFolder));
        });

    }

    public LocalIcons() {
        this(DEFAULT_ICONS_FOLDER);
    }

    /**
     * Provides an URL for a locally served icon.
     *
     * @return an url pointing to a file or a data url
     */
    Optional<String> getIconUrl(String icon) {
        if (!StringUtils.hasLength(icon)) {
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

    public String getDefaultGroupIcon() {
        return defaultGroupIcon;
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

        if (path.startsWith(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64)) {
            return Optional.of(path);
        }

        Optional<String> dataUrl = DataUrlHelper.asBase64(path).map(s -> DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + s);
        dataUrl.ifPresentOrElse(
                s -> iconDataUrls.put(path, s),
                () -> LOGGER.warn("Failed to load svg icon {}", path)
        );
        return dataUrl;
    }
}
