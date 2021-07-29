package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.RelationType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A directed relation between two landscape items. Also known as edge in a directed graph.")
public class RelationDescription {

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

    public RelationDescription() {
    }

    public RelationDescription(String source, String target) {
        this.source = source;
        this.target = target;
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
}
