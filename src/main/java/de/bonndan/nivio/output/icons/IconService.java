package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.model.Item;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;


/**
 * Provides the builtin icons (shipped with nivio) and vendor icons (loaded form remote locations) as embeddable data.
 *
 *
 */
@Service
public class IconService {

    private final LocalIcons localIcons;
    private final ExternalIcons externalIcons;
    private final IconMapping iconMapping;

    public IconService(LocalIcons localIcons, ExternalIcons externalIcons) {
        this.localIcons = localIcons;
        this.externalIcons = externalIcons;
        this.iconMapping = new IconMapping();
    }

    /**
     * Returns the proper icon url for an item.
     *
     * item.icon has precedence over item label "type".
     *
     * @param item landscape item
     * @return the string representation of an URL or data url.
     */
    @Nullable
    public String getIconUrl(Item item) {

        //icon label based
        String icon = item.getIcon();
        if (StringUtils.hasLength(icon)) {

            if (icon.startsWith(DataUrlHelper.DATA_IMAGE)) {
                return icon;
            }

            if (icon.startsWith(ExternalIcons.VENDOR_PREFIX)) {
                String key = icon.replace(ExternalIcons.VENDOR_PREFIX, "").toLowerCase();
                return externalIcons.getUrl(key).orElse(localIcons.getDefaultIcon());
            }

            Optional<String> iconUrl = localIcons.getIconUrl(icon);
            if(iconUrl.isPresent()) {
                if (iconUrl.get().startsWith("http")) {
                    try {
                        return externalIcons.getUrl(new URL(iconUrl.get())).orElse(iconUrl.get());
                    } catch (MalformedURLException ignored) {

                    }
                }
                return iconUrl.get();
            }
        }

        //type based
        String type = item.getType();
        if (!StringUtils.hasLength(type)) {
            return localIcons.getDefaultIcon();
        }

        //fallback to item.type
        String iconName = iconMapping.getIcon(type.toLowerCase()).orElseGet(type::toLowerCase);
        return localIcons.getIconUrl(iconName).orElse(localIcons.getDefaultIcon());
    }

    /**
     * Returns a data url for the given url.
     *
     * @param url external url
     * @return data-url
     */
    public Optional<String> getExternalUrl(URL url) {
        return externalIcons.getUrl(url);
    }
}
