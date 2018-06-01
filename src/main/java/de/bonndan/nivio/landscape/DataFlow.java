package de.bonndan.nivio.landscape;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * Indication of an incoming or outgoing data flow.
 *
 * Outgoing flows having a target which matches a service identifier will cause a relation to be created.
 */
@RelationshipEntity(type = "DATAFLOW")
public class DataFlow {

    @Property(name="description")
    private String description;

    @Property(name="form")
    private String format;

    @StartNode
    Service origin;

    @EndNode
    Service target;

    public DataFlow(Service origin, Service target) {
        this.origin = origin;
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
}
