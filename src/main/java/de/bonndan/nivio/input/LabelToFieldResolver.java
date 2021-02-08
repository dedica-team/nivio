package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.model.Linked;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Inspects item description labels for keys starting with "nivio" and tries to set the corresponding values to fields.
 */
public class LabelToFieldResolver extends Resolver {

    public static final String NIVIO_LABEL_PREFIX = "nivio.";
    public static final String COLLECTION_DELIMITER = ",";

    public LabelToFieldResolver(ProcessLog logger) {
        super(logger);
    }

    @Override
    public void resolve(LandscapeDescription input) {
        input.getItemDescriptions().all().forEach(item -> {
            List<Map.Entry<String, String>> nivioLabels = item.getLabels().entrySet().stream()
                    .filter(entry -> entry.getKey().toLowerCase().startsWith(NIVIO_LABEL_PREFIX))
                    .collect(Collectors.toList());

            nivioLabels.forEach(entry -> {
                String field = entry.getKey().substring(LabelToFieldResolver.NIVIO_LABEL_PREFIX.length());
                setValue(item, field, entry.getValue());
            });

            nivioLabels.forEach(entry -> {
                item.getLabels().remove(entry.getKey());
            });

        });
    }

    private Optional<PropertyDescriptor> getDescriptor(String name) {
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(ItemDescription.class);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getName().equalsIgnoreCase(name))
                return Optional.of(propertyDescriptor);
        }
        return Optional.empty();
    }

    private void setValue(ItemDescription item, String name, String value) {

        if (StringUtils.isEmpty(value)) {
            return;
        }

        Optional<PropertyDescriptor> descriptor = getDescriptor(name);
        if (descriptor.isEmpty()) {
            processLog.debug(String.format("Failed to write field '%s' via label: unknown", name));
            return;
        }

        name = descriptor.get().getName();
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
                String[] o = getParts(value); // value is only a list of strings
                Map<String, Object> propertyValue = (Map<String, Object>) myAccessor.getPropertyValue(name);
                if (propertyValue != null) {
                    for (int i = 0; i < o.length; i++) {
                        if ("links".equals(name)) {
                            processLog.warn("Found deprecated label named links.");
                            try {
                                propertyValue.put(String.valueOf(i + 1), new Link(new URL(o[i])));
                            } catch (MalformedURLException e) {
                                processLog.warn("Failed to parse link " + o[i]);
                            }
                        } else {
                            propertyValue.put(String.valueOf(i + 1), o[i]);
                        }
                    }
                }
                return;
            }

            if (name.startsWith(Linked.LINK_LABEL_PREFIX)) {
                item.setLink(name.replace(Linked.LINK_LABEL_PREFIX, ""), new URL(value));
            } else {
                myAccessor.setPropertyValue(name, value.trim());
            }
        } catch (NotWritablePropertyException e) {
            processLog.debug("Failed to write field '" + name + "' via label");
            item.getLabels().put(name, value);
        } catch (MalformedURLException e) {
            processLog.warn("Failed to add link '" + name + "' via label because of malformed URL " + value);
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
