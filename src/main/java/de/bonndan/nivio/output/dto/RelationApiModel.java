package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    public final URI source;

    public final URI target;

    public final String description;

    public final String format;

    public final RelationType type;

    public final String name;

    public final URI id;

    public final String direction;

    public final Map<String, String> labels;

    public RelationApiModel(@NonNull final Relation relation, @NonNull final Item owner) {
        source = relation.getSource().getFullyQualifiedIdentifier();
        target = relation.getTarget().getFullyQualifiedIdentifier();
        description = relation.getDescription();
        format = relation.getFormat();
        type = RelationType.from(relation.getType());
        id = relation.getFullyQualifiedIdentifier();
        labels = relation.getLabels();

        if (relation.getSource().equals(owner)) {
            name = !StringUtils.hasLength(relation.getTarget().getName()) ?
                    relation.getTarget().getIdentifier() : relation.getTarget().getName();
            direction = OUTBOUND;
        } else {
            name = !StringUtils.hasLength(relation.getSource().getName()) ?
                    relation.getSource().getIdentifier() : relation.getSource().getName();
            direction = INBOUND;
        }
    }
}
