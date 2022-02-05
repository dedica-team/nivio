package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.GraphComponent;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Relation;
import de.bonndan.nivio.model.RelationType;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelationApiModel {

    public static final String INBOUND = "inbound";
    public static final String OUTBOUND = "outbound";

    @JsonIdentityReference(alwaysAsId = true)
    public final GraphComponent source;

    @JsonIdentityReference(alwaysAsId = true)
    public final GraphComponent target;

    public final String description;

    public final String format;

    public final RelationType type;

    public final String name;

    public final URI id;

    public final String direction;

    public final Map<String, String> labels;

    public RelationApiModel(@NonNull final Relation relation, @NonNull final Item owner) {
        source = relation.getSource();
        target = relation.getTarget();
        description = relation.getDescription();
        format = relation.getFormat();
        type = RelationType.from(relation.getType());
        id = relation.getFullyQualifiedIdentifier();
        labels = relation.getLabels();

        if (source.equals(owner)) {
            name = !StringUtils.hasLength(target.getName()) ? target.getIdentifier() : target.getName();
            direction = OUTBOUND;
        } else {
            name = !StringUtils.hasLength(source.getName()) ? source.getIdentifier() : source.getName();
            direction = INBOUND;
        }
    }
}
