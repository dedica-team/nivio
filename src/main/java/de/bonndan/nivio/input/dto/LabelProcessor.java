package de.bonndan.nivio.input.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LabelProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelProcessor.class);
    public static final String NIVIO_LABEL_PREFIX = "nivio.";

    private static final List<String> keyBlacklist = Arrays.asList("secret", "pass", "credentials", "token");

    /**
     * Copies label key and value to item labels, or, if prefixed "nivio.", sets the field to the value.
     *
     * @param item  landscape item
     * @param key   label name
     * @param value label value
     */
    public static void applyLabel(ItemDescription item, String key, Object value) {

        if (inBlacklist(key)) {
            return;
        }
        if (!key.toLowerCase().startsWith(NIVIO_LABEL_PREFIX)) {
            item.getLabels().put(key, (String) value);
            return;
        }

        String field = key.substring(NIVIO_LABEL_PREFIX.length());
        setValue(item, field, (String) value);
    }

    private static boolean inBlacklist(String key) {
        String lk = key.toLowerCase();
        return keyBlacklist.stream().anyMatch(lk::contains);
    }

    private static void setValue(ItemDescription item, String name, String value) {

        if (StringUtils.isEmpty(value)) {
            return;
        }

        PropertyAccessor myAccessor = PropertyAccessorFactory.forBeanPropertyAccess(item);
        Class<?> propertyType = myAccessor.getPropertyType(name);


        try {
            if (propertyType != null && propertyType.isAssignableFrom(List.class)) {
                String[] o = getParts(value);
                myAccessor.setPropertyValue(name, Arrays.asList(o));
                return;
            }
            if (propertyType != null && propertyType.isAssignableFrom(Set.class)) {
                String[] o = getParts(value);
                myAccessor.setPropertyValue(name, Set.of(o));
                return;
            }

            myAccessor.setPropertyValue(name, value.trim());
        } catch (NotWritablePropertyException e) {
            LOGGER.warn("Failed to write field {} via label", name);
        }
    }

    private static String[] getParts(String value) {
        String[] split = StringUtils.split(value, ",");
        if (split == null) {
            return new String[]{value.trim()};
        }

        return Arrays.stream(split).map(String::trim).toArray(String[]::new);
    }
}
