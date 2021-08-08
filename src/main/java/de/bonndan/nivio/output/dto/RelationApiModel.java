package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Relation;
import de.bonndan.nivio.model.RelationType;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelationApiModel {

    public static final String INBOUND = "inbound";
    public static final String OUTBOUND = "outbound";

    @JsonIdentityReference(alwaysAsId = true)
    public final Item source;

    @JsonIdentityReference(alwaysAsId = true)
    public final Item target;

    public final String description;

    public final String format;

    public final RelationType type;

    public final String name;

    public final String id;

    public final String direction;

    public final Map<String, String> labels;

    public RelationApiModel(@NonNull final Relation relation, @NonNull final Item owner) {
        source = relation.getSource();
        target = relation.getTarget();
        description = relation.getDescription();
        format = relation.getFormat();
        type = relation.getType();
        id = relation.getIdentifier();
        labels = relation.getLabels();

        if (relation.getSource().equals(owner)) {
            name = !StringUtils.hasLength(target.getName()) ? target.getIdentifier() : target.getName();
            direction = OUTBOUND;
        } else {
            name = !StringUtils.hasLength(source.getName()) ? source.getIdentifier() : source.getName();
            direction = INBOUND;
        }
    }
}
