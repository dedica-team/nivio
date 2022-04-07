package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Relation;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelationApiModel extends ComponentApiModel {

    public static final String INBOUND = "inbound";
    public static final String OUTBOUND = "outbound";

    public final URI source;

    private final URI target;

    private final String format;

    private final String direction;

    private final String relationName;

    private final Map<String, URI> processes;

    public RelationApiModel(@NonNull final Relation relation, @NonNull final Item owner) {
        super(relation);
        source = relation.getSource().getFullyQualifiedIdentifier();
        target = relation.getTarget().getFullyQualifiedIdentifier();
        format = relation.getFormat();
        processes = relation.getProcesses();

        if (relation.getSource().equals(owner)) {
            relationName = !StringUtils.hasLength(relation.getTarget().getName()) ?
                    relation.getTarget().getIdentifier() : relation.getTarget().getName();
            direction = OUTBOUND;
        } else {
            relationName = !StringUtils.hasLength(relation.getSource().getName()) ?
                    relation.getSource().getIdentifier() : relation.getSource().getName();
            direction = INBOUND;
        }
    }

    @Override
    public String getName() {
        return relationName;
    }

    public URI getSource() {
        return source;
    }

    public URI getTarget() {
        return target;
    }

    public String getFormat() {
        return format;
    }

    public String getDirection() {
        return direction;
    }

    public Map<String, URI> getProcesses() {
        return processes;
    }
}
