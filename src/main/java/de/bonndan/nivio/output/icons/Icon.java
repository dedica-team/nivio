package de.bonndan.nivio.output.icons;

import java.net.URL;

/**
 * Icon for a service.
 */
class Icon {

    private final URL url;
    private boolean isCustom = false;

    public Icon(URL url, boolean isCustom) {
        this.url = url;
        this.isCustom = isCustom;
    }

    public Icon(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    /**
     *
     * @return true if the source/url is a custom url
     */
    public boolean isCustom() {
        return isCustom;
    }
}
