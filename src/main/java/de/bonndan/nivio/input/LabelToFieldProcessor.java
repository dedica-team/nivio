package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.LandscapeImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Inspects item description labels for keys starting with "nivio" and tries to set the corresponding values to fields.
 *
 *
 *
 */
public class LabelToFieldProcessor {

    public static final String NIVIO_LABEL_PREFIX = "nivio.";
    public static final String COLLECTION_DELIMITER = ",";

    private final ProcessLog logger;

    public LabelToFieldProcessor(ProcessLog logger) {
        this.logger = logger;
    }

    public void process(LandscapeDescription input, LandscapeImpl landscape) {
        input.getItemDescriptions().all().forEach(item -> {
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

            if (propertyType != null && propertyType.isAssignableFrom(Map.class)) {
                String[] o = getParts(value);
                Map propertyValue = (Map) myAccessor.getPropertyValue(name);
                for (int i = 0; i < o.length; i++) {
                    if (ItemDescription.LINKS_FIELD.equals(name)) {
                        try {
                            propertyValue.put(String.valueOf(i+1), new URL(o[i]));
                        } catch (MalformedURLException e) {
                            logger.warn("Failed to parse link " + o[i]);
                        }
                    } else {
                        propertyValue.put(String.valueOf(i+1), o[i]);
                    }
                }
                return;
            }

            myAccessor.setPropertyValue(name, value.trim());
        } catch (NotWritablePropertyException e) {
            logger.warn("Failed to write field '" + name + "' via label");
        }
    }

    private static String[] getParts(String value) {
        String[] split = StringUtils.split(value, COLLECTION_DELIMITER);
        if (split == null) {
            return new String[]{value.trim()};
        }

        return Arrays.stream(split).map(String::trim).toArray(String[]::new);
    }
}
