package de.bonndan.nivio.landscape;

/**
 * One specific
 */
public interface StatusItem {

    String HEALTH = "health";
    String LIFECYCLE = "lifecycle";
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
