package de.bonndan.nivio.model;

/**
 * One specific
 */
public interface StatusItem {

    String HEALTH = "health";
    String SECURITY = "security";
    String STABILITY = "stability";
    String CAPABILITY = "capability";

    /**
     * The label / name, unique for a service.
     */
    String getLabel();

    Status getStatus();

    String getMessage();
}
