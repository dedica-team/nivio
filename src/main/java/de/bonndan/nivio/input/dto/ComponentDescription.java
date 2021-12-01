package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Linked;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Base interface for input DTOs, which are mutable objects.
 */
public interface ComponentDescription extends Component, Labeled, Linked {

    void setName(String name);

    void setDescription(String description);

    void setOwner(String owner);

    void setContact(String contact);

    void setIcon(String icon);

    default void setLabel(String key, String value) {
        setLabel(key, (Object) value);
    }

    /**
     * Any-setter default implementation for deserialization.
     *
     * @param key   label key
     * @param value label value (string|string[]|number|list|map)
     */
    @JsonAnySetter
    default void setLabel(@NonNull final String key, final Object value) {
        if (!StringUtils.hasLength(key)) {
            throw new IllegalArgumentException("Label key is empty.");
        }

        if (value instanceof String) {
            getLabels().put(key.toLowerCase(), (String) value);
            return;
        }

        if (value instanceof Number) {
            getLabels().put(key.toLowerCase(), String.valueOf(value));
            return;
        }

        if (value instanceof String[]) {
            Arrays.stream(((String[]) value)).forEach(s -> setPrefixed(key, s));
            return;
        }

        if (value instanceof List) {
            try {
                //noinspection unchecked,rawtypes
                ((List) value).forEach(s -> setPrefixed(key, (String) s));
                return;
            } catch (ClassCastException e) {
                throw new ProcessingException(String.format("Cannot set '%s' to list '%s'. Is this a list-like structure", key, value), e);
            }
        }

        if (value instanceof Map) {
            throw new IllegalArgumentException(String.format("Cannot use the value of '%s' as map ('%s'). Please check the spelling of", key, value));
        }

        getLabels().put(key, String.valueOf(value));
    }
}
