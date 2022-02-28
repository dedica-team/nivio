package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeDescriptionFactory;
import de.bonndan.nivio.util.URIHelper;
import de.bonndan.nivio.util.URLFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * Masks all label values which might contain secrets.
 *
 * Replaces value completely unless it is an url containing userinfo.
 */
public class SecureLabelsResolver implements Resolver {

    private static final List<String> keyBlacklist = Arrays.asList("secret", "pass", "credentials", "token", "key");
    public static final String MASK = "*";

    @NonNull
    @Override
    public LandscapeDescription resolve(LandscapeDescription input) {

        input.getReadAccess().all(ItemDescription.class).forEach(itemDescription -> {
            Map<String, Object> cleaned = new HashMap<>();
            itemDescription.getLabels().forEach((s, s2) -> {
                Optional<Object> cleanedValue = getCleanedValue(s, s2);
                cleaned.put(s, cleanedValue.orElse(null));
            });
            cleaned.forEach((s, o) -> itemDescription.setLabel(s, String.valueOf(o)));
        });

        return LandscapeDescriptionFactory.refreshedCopyOf(input);
    }

    /**
     * Copies label key and value to item labels.
     *
     * @param key   label name
     * @param value label value
     */
    private Optional<Object> getCleanedValue(String key, Object value) {

        if (inBlacklist(key)) {
            return Optional.of(MASK);
        }

        return getWithoutSecret(value);
    }

    private Optional<Object> getWithoutSecret(Object value) {
        if (!(value instanceof String))
            return Optional.empty();

        Optional<URL> url = URLFactory.getURL((String) value);
        if (url.isPresent()) {
            return Optional.ofNullable(replaceIfSecret(url.get()));
        }

        Optional<URI> uri = URIHelper.getURIWithHostAndScheme((String) value);
        if (uri.isPresent()) {
            return Optional.ofNullable(replaceIfSecret(uri.get()));
        }
        return Optional.of(value);
    }

    private String replaceIfSecret(URL url) {
        String userInfo = url.getUserInfo();
        if (!StringUtils.isEmpty(userInfo)) {
            String s = url.getProtocol() + "://*@" + url.getHost();

            if (url.getPort() != -1) {
                s += ":" + url.getPort();
            }

            if (url.getPath() != null) {
                s += url.getPath();
            }

            if (url.getQuery() != null) {
                s += "?" + url.getQuery();
            }
            return s;
        }
        return url.toString();
    }

    private String replaceIfSecret(URI uri) {
        String userInfo = uri.getUserInfo();
        if (StringUtils.hasLength(userInfo)) {
            String s = uri.getScheme() + "://*@" + uri.getHost();

            if (uri.getPort() != -1) {
                s += ":" + uri.getPort();
            }

            if (uri.getPath() != null) {
                s += uri.getPath();
            }

            if (uri.getQuery() != null) {
                s += "?" + uri.getQuery();
            }
            return s;
        }
        return uri.toString();
    }

    private static boolean inBlacklist(String key) {
        String lk = key.toLowerCase();
        return keyBlacklist.stream().anyMatch(lk::contains);
    }
}
