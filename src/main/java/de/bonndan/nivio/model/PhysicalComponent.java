package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * Physical components "exist"
 */
public interface PhysicalComponent {

    /**
     * Returns the relations including the source and target components
     */
    @NonNull
    Set<Relation> getRelations();
}
