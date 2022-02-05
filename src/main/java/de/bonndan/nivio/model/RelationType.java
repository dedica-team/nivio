package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public enum RelationType {
    CHILD,
    DATAFLOW,
    PROVIDER;

    @JsonCreator
    public static RelationType from(String relationType) {
        if (!StringUtils.hasLength(relationType)) {
            return DATAFLOW;
        }

        if (PROVIDER.name().toLowerCase(Locale.ROOT).equalsIgnoreCase(relationType)) {
            return PROVIDER;
        }

        return DATAFLOW;
    }

    /**
     * @param relations unfiltered relations
     * @return filtered by type
     */
    public Set<Relation> filter(Set<Relation> relations) {
        return relations.stream()
                .filter(relationItem -> this.name().toLowerCase(Locale.ROOT).equals(relationItem.getType()))
                .collect(Collectors.toSet());
    }
}
