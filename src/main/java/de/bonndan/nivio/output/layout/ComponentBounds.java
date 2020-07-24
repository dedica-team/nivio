package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Component;

import java.util.List;

/**
 * A value object to hold dimensions and position data of rendered {@link Component}s.
 *
 * Also holds children to allow recursive operations.
 */
public class ComponentBounds {

    private final Component component;

    public double x = 0;
    public double y = 0;
    public double width = 50;
    public double height = 50;
    private final List<Component> opposites;
    private List<ComponentBounds> children;

    public ComponentBounds(Component component, List<Component> opposites) {
        this.component = component;
        this.opposites = opposites;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getWidth() {
        return width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public List<Component> getOpposites() {
        return opposites;
    }

    public Component getComponent() {
        return component;
    }

    public void setChildren(List<ComponentBounds> children) {
        this.children = children;
    }

    public List<ComponentBounds> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "ComponentBounds{" +
                "component=" + component +
                '}';
    }
}
