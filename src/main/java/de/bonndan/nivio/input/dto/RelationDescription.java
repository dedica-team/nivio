package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.search.ComponentMatcher;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;


@Schema(description = "A directed relation between two landscape items. Also known as edge in a directed graph.")
public class RelationDescription implements Labeled {

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
        setSource(source);
        setTarget(target);
    }

    public static boolean validateEndpoint(@Nullable final String endpoint) {
        return StringUtils.hasLength(endpoint);
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
        if (!validateEndpoint(source)) {
            throw new IllegalArgumentException(String.format("Invalid source identifier used: '%s'", source));
        }
        if (Objects.equals(source, target)) {
            throw new IllegalArgumentException(String.format("setSource: Source %s and target %s are equal.", source, target));
        }
        this.source = source;
    }

    public void setTarget(String target) {
        if (!validateEndpoint(target)) {
            throw new IllegalArgumentException(String.format("Invalid target identifier used: '%s'", target));
        }
        if (Objects.equals(source, target)) {
            throw new IllegalArgumentException(String.format("setTarget: Source %s and target %s are equal.", source, target));
        }
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
                .filter(rel -> {
                    try {
                        return matches(source, rel.getSource()) && matches(target, rel.getTarget());
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                })
                .findFirst();
    }

    private boolean matches(String end1, String end2) {
        var m1 = ComponentMatcher.forComponent(end1);
        var m2 = ComponentMatcher.forComponent(end2);
        return m1.equals(m2);
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
