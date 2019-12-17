package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.LandscapeImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Inspects item description labels for keys starting with "nivio" and tries to set the corresponding values to fields.
 *
 *
 *
 */
public class LabelToFieldProcessor {

    public static final String NIVIO_LABEL_PREFIX = "nivio.";

    private final ProcessLog logger;

    public LabelToFieldProcessor(ProcessLog logger) {
        this.logger = logger;
    }

    public void process(LandscapeDescription input, LandscapeImpl landscape) {
        input.getItemDescriptions().forEach(item -> {
            item.getLabels().entrySet().stream()
                    .filter(entry -> entry.getKey().toLowerCase().startsWith(NIVIO_LABEL_PREFIX))
                    .forEach(entry -> {
                        String field = entry.getKey().substring(LabelToFieldProcessor.NIVIO_LABEL_PREFIX.length());
                        setValue(item, field, entry.getValue());
                    });
        });
    }

    private void setValue(ItemDescription item, String name, String value) {

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
            logger.warn("Failed to write field '" + name + "' via label");
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
