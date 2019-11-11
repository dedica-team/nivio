package de.bonndan.nivio.input.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class LabelProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelProcessor.class);
    public static final String NIVIO_LABEL_PREFIX = "nivio.";

    /**
     * Copies label key and value to item labels, or, if prefixed "nivio.", sets the field to the value.
     *
     * @param item landscape item
     * @param key  label name
     * @param value label value
     */
    public static void applyLabel(ItemDescription item, String key, Object value) {

        if (!key.toLowerCase().startsWith(NIVIO_LABEL_PREFIX)) {
            item.getLabels().put(key, (String) value);
            return;
        }

        String field = key.substring(NIVIO_LABEL_PREFIX.length());
        setValue(item, field, (String) value);
    }

    private static void setValue(ItemDescription item, String name, String value) {
        Field[] declaredFields = item.getClass().getDeclaredFields();
        Optional<Field> field = Arrays.stream(declaredFields).filter(field1 -> field1.getName().equals(name)).findFirst();
        field.ifPresent(field1 -> {
            try {
                field1.setAccessible(true);
                field1.set(item, value);
                field1.setAccessible(false);
            } catch (IllegalAccessException e) {
                LOGGER.warn("Failed to set field {} via label: " + e.getMessage(), name);
            }
        });
    }
}
