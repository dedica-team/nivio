package de.bonndan.nivio.util;

import java.net.URL;
import java.util.Objects;

public class URLHelper {

    public static boolean isLocal(URL url) {
        return Objects.nonNull(url) && url.toString().startsWith("file://");
    }
}
