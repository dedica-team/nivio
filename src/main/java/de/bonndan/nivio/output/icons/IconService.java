package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.model.Item;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;


/**
 * Provides the builtin icons (shipped with nivio) and vendor icons (loaded form remote locations) as embeddable data.
 *
 *
 */
@Service
public class IconService {

    private final LocalIcons localIcons;
    private final VendorIcons vendorIcons;

    public IconService(LocalIcons localIcons, VendorIcons vendorIcons) {
        this.localIcons = localIcons;
        this.vendorIcons = vendorIcons;
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
        if (!StringUtils.isEmpty(icon)) {

            if (icon.startsWith(VendorIcons.VENDOR_PREFIX)) {
                String key = icon.replace(VendorIcons.VENDOR_PREFIX, "").toLowerCase();
                return vendorIcons.getUrl(key).orElse(localIcons.getDefaultIcon());
            }

            Optional<String> iconUrl = localIcons.getIconUrl(icon);
            if(iconUrl.isPresent()) {
                return iconUrl.get();
            }
        }

        //type based
        String type = item.getType();
        if (StringUtils.isEmpty(type)) {
            return localIcons.getDefaultIcon();
        }

        //fallback to item.type
        String iconName = IconMapping.of(type.toLowerCase()).map(IconMapping::getIcon).orElseGet(type::toLowerCase);
        return localIcons.getIconUrl(iconName).orElse(localIcons.getDefaultIcon());
    }
}
