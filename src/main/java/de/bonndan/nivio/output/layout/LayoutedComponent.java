package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A value object to hold dimensions and position data of rendered {@link Component}s.
 *
 * Also holds children to allow recursive operations.
 */
public class LayoutedComponent {

    private final Component component;

    public double x = 0;
    public double y = 0;
    public double width = 50;
    public double height = 50;
    private final List<Component> opposites;
    private List<LayoutedComponent> children;
    private String fill;
    private String icon;
    private String color;

    public LayoutedComponent(Component component, List<Component> opposites) {
        this.component = component;
        this.opposites = opposites;
    }

    public LayoutedComponent(Component component) {
        this.component = component;
        opposites = new ArrayList<>();
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

    public void setChildren(List<LayoutedComponent> children) {
        this.children = children;
    }

    public List<LayoutedComponent> getChildren() {
        return children;
    }

    public String getFill() {
        return fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    @Override
    public String toString() {
        return "LayoutedComponent{" +
                "component=" + component +
                '}';
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
