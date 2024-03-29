package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.search.ItemMatcher;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Null;
import java.util.*;


@Schema(description = "A directed relation between two landscape items. Also known as edge in a directed graph.")
public class RelationDescription implements Labeled {

    public static final String ENDPOINT_VALIDATION = "^[\\w.:\\-/]{2,256}$";

    @Schema(description = "The type of the relation, i.e. whether it is a hard or a soft dependency.")
    private RelationType type;

    @Schema(description = "A textual explanation of the nature or function of the relation.")
    private String description;

    @Schema(description = "A textual explanation of payload.", example = "JSON|XML|...")
    private String format;

    @Schema(description = "The item identifier of the source. Prepend a group identifier if the simple item identifier is ambiguous.", example = "myService|groupA/myOtherService")
    private String source;

    @Schema(description = "The item identifier of the target. Prepend a group identifier if the simple item identifier is ambiguous.", example = "dataSink|groupB/dataSink")
    private String target;

    @Schema(description = "Key-value pair labels for a relation.")
    private final Map<String, String> labels = new HashMap<>();

    public RelationDescription() {
    }

    public RelationDescription(@NonNull final String source, @NonNull final String target) {
        if (!validateEndpoint(source)) {
            throw new IllegalArgumentException(String.format("Invalid source identifier used: '%s'", source));
        }
        if (!validateEndpoint(target)) {
            throw new IllegalArgumentException(String.format("Invalid target identifier used: '%s'", target));
        }
        this.source = source.trim();
        this.target = target.trim();
    }

    public static boolean validateEndpoint(@Nullable final String endpoint) {
        return endpoint != null && endpoint.trim().matches(ENDPOINT_VALIDATION);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getTarget() {
        return target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public RelationType getType() {
        return type;
    }

    public void setType(RelationType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RelationDescription{" +
                "type=" + type +
                ", description='" + description + '\'' +
                ", format='" + format + '\'' +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                '}';
    }

    @Override
    public String getLabel(String key) {
        return getLabels().get(key);
    }

    @Override
    @NonNull
    public Map<String, String> getLabels() {
        return labels;
    }

    @Override
    @JsonAnySetter
    public void setLabel(@NonNull final String key, @Nullable final String value) {
        Labeled.super.setLabel(key, value);
    }

    /**
     * Finds the first relation description with same source and target.
     *
     * @param relations a collection of existing relations
     * @return the sibling
     */
    Optional<RelationDescription> findMatching(@NonNull final Collection<RelationDescription> relations) {
        return Objects.requireNonNull(relations).stream()
                .filter(rel -> matches(source, rel.getSource()))
                .filter(rel -> matches(target, rel.getTarget()))
                .findFirst();
    }

    private boolean matches(String end1, String end2) {
        Optional<ItemMatcher> m1 = ItemMatcher.forTarget(end1);
        Optional<ItemMatcher> m2 = ItemMatcher.forTarget(end2);
        return m1.isPresent() && m2.isPresent() && m1.map(m -> m.equals(m2.get())).orElse(false);
    }

    /**
     * Updates the current object with values from the param.
     *
     * @param newer update
     */
    public void update(@NonNull final RelationDescription newer) {
        Objects.requireNonNull(newer);

        setDescription(newer.description);
        setFormat(newer.format);

        Labeled.merge(newer, this);
    }
}
