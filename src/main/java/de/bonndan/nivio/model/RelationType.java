package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.util.StringUtils;

public enum RelationType {
    DATAFLOW,
    PROVIDER
;
    @JsonCreator
    public static RelationType from(String relationType) {
        if (StringUtils.isEmpty(relationType))
            return DATAFLOW;

        if ("provider".equals(relationType.toLowerCase()))
            return PROVIDER;

        return DATAFLOW;
    }

}
