package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ComponentDescription;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;

/**
 * Base class for component builders.
 *
 * https://ducmanhphan.github.io/2020-04-06-how-to-apply-builder-pattern-with-inhertitance/
 *
 * @param <B> Builder
 * @param <O> Output
 * @param <P> Parent
 */
public abstract class GraphNodeBuilder<B extends GraphNodeBuilder<B, O, P>, O extends GraphComponent, P extends GraphComponent> {

    protected Map<String, Link> links = new HashMap<>();
    protected Map<String, String> labels = new ConcurrentHashMap<>();
    protected String identifier;
    protected String name;
    protected String owner;
    protected String contact;
    protected String description;
    protected String type;
    protected P parent;

    public abstract B getThis();

    public B withLinks(Map<String, Link> links) {
        this.links = links;
        return getThis();
    }

    public B withLabels(Map<String, String> labels) {
        this.labels = labels;
        return getThis();
    }

    public B withIdentifier(String identifier) {
        this.identifier = identifier;
        return getThis();
    }

    public B withName(String name) {
        this.name = name;
        return getThis();
    }

    public B withOwner(String owner) {
        this.owner = owner;
        return getThis();
    }

    public B withContact(String contact) {
        this.contact = contact;
        return getThis();
    }

    public B withDescription(String description) {
        this.description = description;
        return getThis();
    }

    public B withType(String type) {
        this.type = type;
        return getThis();
    }

    public B withParent(P parent) {
        this.parent = parent;
        return getThis();
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public B withComponentDescription(@Nullable final ComponentDescription description) {
        if (description != null) {
            if (!StringUtils.hasLength(identifier)) {
                identifier = description.getIdentifier();
            }
            withName(description.getName());
            withType(description.getType());
            withContact(description.getContact());
            withOwner(description.getOwner());
            withDescription(description.getDescription());
            description.getLinks().forEach((s, url) -> getLinks().putIfAbsent(s, url));
            description.getLabels().forEach((s, val) -> getLabels().putIfAbsent(s, val));

            assignSafe(description.getColor(), s -> getLabels().put(Label.color.name(), s));
            assignSafe(description.getIcon(), s -> getLabels().put(Label.icon.name(), s));
        }
        return getThis();
    }

    public abstract O build();

}
