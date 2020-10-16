package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static de.bonndan.nivio.output.icons.IconMapping.DEFAULT_ICON;

/**
 * Provides the builtin icons (shipped with nivio) and vendor icons (loaded form remote locations) as embeddable data.
 *
 *
 */
@Service
public class LocalIcons {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalIcons.class);

    private final VendorIcons vendorIcons;

    /**
     * default icon data url
     */
    private final String defaultIcon;

    /**
     * data url cache
     */
    private final Map<String, String> iconDataUrls = new ConcurrentHashMap<>();


    public LocalIcons(VendorIcons vendorIcons) {

        this.vendorIcons = vendorIcons;
        defaultIcon = getIconUrl(DEFAULT_ICON.getIcon(), false);
    }

    /**
     * Returns the proper icon url for an item.
     *
     * item.icon has precedence over item label "type".
     *
     * @param item landscape item
     * @return the string representation of an URL or data url.
     */
    public String getIconUrl(Item item) {

        //icon label based
        String icon = item.getIcon();
        if (!StringUtils.isEmpty(icon)) {

            if (icon.startsWith(VendorIcons.VENDOR_PREFIX)) {
                String key = icon.replace(VendorIcons.VENDOR_PREFIX, "").toLowerCase();
                return vendorIcons.getUrl(key).orElse(defaultIcon);
            }

            String iconUrl = getIconUrl(icon, true);
            if(iconUrl != null) {
                return iconUrl;
            }
        }

        //type based
        String type = item.getLabel(Label.type);
        if (StringUtils.isEmpty(type)) {
            return getIconUrl(DEFAULT_ICON.getIcon(), false);
        }

        //fallback to item.type
        String iconName = IconMapping.of(type.toLowerCase()).map(IconMapping::getIcon).orElseGet(type::toLowerCase);
        return getIconUrl(iconName, true);
    }

    /**
     * Provides an URL for a locally served icon.
     *
     * @return an url pointing to a file or a data url
     */
    String getIconUrl(String icon, boolean fallback) {
        URL url = URLHelper.getURL(icon).orElse(null);

        //local icon urls are not supported
        if (url != null && URLHelper.isLocal(url)) {
            url = null;
        }

        if (url == null) {
            String iconFile = "/static/icons/svg/" + icon + ".svg";
            return asSVGDataUrl(iconFile).orElse(fallback ? getIconUrl(DEFAULT_ICON.getIcon(), false) : null);
        }

        return url.toString();
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
