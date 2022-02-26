package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.model.RelationFactory;
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
    public static final String LINK_LABEL_PREFIX = "link.";
    public static final String RELATIONS_LABEL_PREFIX = "relations.";
    public static final String LINKS_LABEL = "links";
    public static final String MAP_KEY_VALUE_DELIMITER = ":";

    public LabelToFieldResolver(ProcessLog logger) {
        super(logger);
    }

    @Override
    public void resolve(LandscapeDescription input) {
        input.getReadAccess().all(ItemDescription.class).forEach(item -> {
            List<Map.Entry<String, String>> nivioLabels = item.getLabels().entrySet().stream()
                    .filter(entry -> entry.getKey().toLowerCase().startsWith(NIVIO_LABEL_PREFIX))
                    .collect(Collectors.toList());

            nivioLabels.forEach(entry -> {
                String field = entry.getKey().substring(LabelToFieldResolver.NIVIO_LABEL_PREFIX.length());
                setValue(item, field, entry.getValue());
            });

            nivioLabels.forEach(entry -> item.getLabels().remove(entry.getKey()));
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

        if (!StringUtils.hasLength(value)) {
            return;
        }

        Optional<PropertyDescriptor> descriptor = getDescriptor(name);
        if (descriptor.isPresent()) {
            name = descriptor.get().getName();
        }

        if (handleLinksAndFrameworks(item, name, value)) {
            return;
        }

        setUsingAccessor(item, name, value);
    }

    private void setUsingAccessor(ItemDescription item, String name, String value) {
        PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(item);
        Class<?> propertyType = accessor.getPropertyType(name);

        try {
            if (propertyType != null) {
                String[] o = getParts(value);
                if (propertyType.isAssignableFrom(List.class)) {
                    accessor.setPropertyValue(name, Arrays.asList(o));
                    return;
                }

                if (propertyType.isAssignableFrom(Set.class)) {
                    accessor.setPropertyValue(name, Set.of(o));
                    return;
                }

                if (propertyType.isAssignableFrom(Map.class)) {
                    @SuppressWarnings("unchecked") Map<String, Object> propertyValue = (Map<String, Object>) accessor.getPropertyValue(name);
                    if (propertyValue != null) {
                        for (int i = 0; i < o.length; i++) {
                            String key = String.valueOf(i + 1);
                            propertyValue.put(key, o[i]);
                        }
                    }
                    return;
                }
            }

            accessor.setPropertyValue(name, value.trim());

        } catch (NotWritablePropertyException e) {
            processLog.debug(String.format("Failed to write field '%s' via label", name));
            item.getLabels().put(name, value);
        }
    }

    private boolean handleLinksAndFrameworks(ItemDescription item, String name, String value) {
        if (handleLinksInLabels(item, name, value)) return true;
        if (handleSingleLink(item, name, value)) return true;

        if (handleFrameworksInLabels(item, name, value)) return true;

        if (handleRelations(item, name, value)) return true;

        return false;
    }

    private boolean handleSingleLink(ItemDescription item, String name, String value) {
        if (name.startsWith(LINK_LABEL_PREFIX)) {
            try {
                item.setLink(name.replace(LINK_LABEL_PREFIX, ""), new URL(value));
            } catch (MalformedURLException e) {
                processLog.warn(String.format("Failed to add link '%s' via label because of malformed URL %s", name, value));
            }
            return true;
        }
        return false;
    }

    private boolean handleRelations(ItemDescription item, String name, String value) {
        if (name.startsWith(RELATIONS_LABEL_PREFIX)) {
            String[] endpoints = getParts(value);
            boolean isProviders = name.toLowerCase(Locale.ROOT).contains("provider");
            boolean isInbound = name.toLowerCase(Locale.ROOT).contains("inbound");

            Arrays.stream(endpoints)
                    .filter(RelationDescription::validateEndpoint)
                    .map(endpoint -> {
                        if (isProviders) {
                            return RelationFactory.createProviderDescription(endpoint, item.getIdentifier());
                        }
                        if (isInbound) {
                            return new RelationDescription(endpoint, item.getIdentifier());
                        }
                        return new RelationDescription(item.getIdentifier(), endpoint);
                    })
                    .forEach(item::addOrReplaceRelation);

            return true;
        }
        return false;
    }

    private boolean handleFrameworksInLabels(ItemDescription item, String name, String value) {
        if (Label.frameworks.name().equals(name)) {
            String[] o = getParts(value);
            for (String s : o) {
                String[] frameworkParts = s.split(MAP_KEY_VALUE_DELIMITER);
                if (frameworkParts.length == 2) {
                    item.setFramework(frameworkParts[0], frameworkParts[1]);
                }
            }
            return true;
        }
        return false;
    }

    private boolean handleLinksInLabels(ItemDescription item, String name, String value) {
        if (LINKS_LABEL.equals(name)) {
            processLog.info("Found list-style label named 'links'.");
            String[] o = getParts(value);
            for (int i = 0; i < o.length; i++) {
                String key = String.valueOf(i + 1);
                getLink(o[i]).ifPresent(link1 -> item.getLinks().put(key, link1));
            }
            return true;
        }
        return false;
    }

    private static String[] getParts(String value) {
        String[] split = value.split(COLLECTION_DELIMITER);
        return Arrays.stream(split).map(String::trim).toArray(String[]::new);
    }

    private Optional<Link> getLink(String url) {
        try {
            return Optional.of(new Link(new URL(url)));
        } catch (MalformedURLException e) {
            processLog.warn("Failed to parse link " + url);
            return Optional.empty();
        }
    }
}
