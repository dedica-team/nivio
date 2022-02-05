package de.bonndan.nivio.model;

public final class UnitBuilder extends GraphNodeBuilder<UnitBuilder, Unit, Landscape> {

    private UnitBuilder() {
    }

    public static UnitBuilder aTestUnit(String identifier) {
        return new UnitBuilder().withIdentifier(identifier).withParent(LandscapeFactory.createForTesting("test", "test").build());
    }

    @Override
    public UnitBuilder getThis() {
        return this;
    }

    public static UnitBuilder aUnit() {
        return new UnitBuilder();
    }

    @Override
    public Unit build() {
        Unit unit = new Unit(identifier, name, owner, contact, description, type, parent);
        unit.setLinks(links);
        unit.setLabels(labels);
        return unit;
    }
}
