package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.util.URLHelper;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.*;

/**
 * Masks all label values which might contain secrets.
 */
public class SecureLabelsProcessor {

    private static final List<String> keyBlacklist = Arrays.asList("secret", "pass", "credentials", "token", "key");
    public static final String MASK = "*";

    public void process(LandscapeDescription input) {

        input.getItemDescriptions().all().forEach(itemDescription -> {
            Map<String, Object> cleaned = new HashMap<>();
            itemDescription.getLabels().forEach((s, s2) -> getCleanedValue(s, s2).ifPresent(o -> cleaned.put(s, o)));
            cleaned.forEach(itemDescription::setLabel);
        });
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

        return URLHelper.getURL((String) value).map(this::replaceIfSecret);
    }

    private String replaceIfSecret(URL url) {
        String userInfo = url.getUserInfo();
        if (!StringUtils.isEmpty(userInfo)) {
            return url.getProtocol() + "://*@" + url.getHost() + url.getPath() + "?" + url.getQuery();
        }
        return url.toString();
    }

    private static boolean inBlacklist(String key) {
        String lk = key.toLowerCase();
        return keyBlacklist.stream().anyMatch(lk::contains);
    }
}
