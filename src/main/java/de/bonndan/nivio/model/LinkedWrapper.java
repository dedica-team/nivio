package de.bonndan.nivio.model;

/**
 * This is a workaround to use a custom serializer for {@link Linked} in order to generate hateoas links.
 */
public class LinkedWrapper {

    private final Linked component;

    public LinkedWrapper(Linked component) {
        this.component = component;
    }

    public Linked getComponent() {
        return component;
    }
}
