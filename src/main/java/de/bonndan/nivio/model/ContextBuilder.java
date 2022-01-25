package de.bonndan.nivio.model;

public final class ContextBuilder extends GraphNodeBuilder<ContextBuilder, Context, Unit> {

    private ContextBuilder() {
    }

    @Override
    public ContextBuilder getThis() {
        return this;
    }

    public static ContextBuilder aContext() {
        return new ContextBuilder();
    }

    @Override
    public Context build() {
        Context context = new Context(identifier, name, owner, contact, description, type, parent);
        context.setLinks(links);
        context.setLabels(labels);
        return context;
    }
}
