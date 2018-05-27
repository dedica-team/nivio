package de.bonndan.nivio.applayer;



import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NodeEntity
public class Service {

    @Id
    @GeneratedValue private Long id;

    private String type;
    private String name;
    private String serviceName;
    private String owner;
    private String description;

    /**
     * Neo4j doesn't REALLY have bi-directional relationships. It just means when querying
     * to ignore the direction of the relationship.
     * https://dzone.com/articles/modelling-data-neo4j
     */
    @Relationship(type = "READS", direction = Relationship.OUTGOING)
    public Set<Service> reads = new HashSet<>();

    @Relationship(type = "WRITES", direction = Relationship.OUTGOING)
    public Set<Service> writes = new HashSet<>();

    private Service() {
        // Empty constructor required as of Neo4j API 2.0.5
    };

    public Service(Long id, String type, String name, String serviceName, String owner, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.serviceName = serviceName;
        this.owner = owner;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void readsFrom(Service person) {
        reads.add(person);
    }

    public String toString() {

        return this.name + "'s reads => "
                + Optional.ofNullable(this.reads).orElse(
                Collections.emptySet()).stream()
                .map(Service::getName)
                .collect(Collectors.toList());
    }


}
