package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public enum RelationType {
    DATAFLOW,
    PROVIDER;

    @JsonCreator
    public static RelationType from(String relationType) {
        if (StringUtils.isEmpty(relationType)) {
            return DATAFLOW;
        }

        if ("provider".equals(relationType.toLowerCase())) {
            return PROVIDER;
        }

        return DATAFLOW;
    }

    /**
     * Filters a list of relations for equal type.
     *
     * @param all all relations
     * @return filtered subset
     */
    @Deprecated
    public List<RelationItem> filter(Collection<? extends RelationItem> all) {
        return all.stream().filter(relationItem -> this.equals(relationItem.getType())).collect(Collectors.toList());
    }
}
