package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A value object to hold dimensions and position data of rendered {@link Component}s.
 *
 * Also holds children to allow recursive operations.
 */
public class LayoutedComponent {

    private final Component component;

    public long x = 0;
    public long y = 0;
    public double width = 50;
    public double height = 50;
    private final List<Component> opposites;
    private List<LayoutedComponent> children;

    @Nullable
    private String defaultColor;

    public LayoutedComponent(@NonNull final Component component, List<Component> opposites) {
        this.component = Objects.requireNonNull(component);
        this.opposites = opposites;
    }

    public LayoutedComponent(Component component) {
        this.component = component;
        opposites = new ArrayList<>();
    }

    public long getX() {
        return x;
    }

    public long getY() {
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

    public void setX(long x) {
        this.x = x;
    }

    public void setY(long y) {
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
        if (component instanceof Item) {
            return ((Item) component).getLabel(Label._filldata);
        }

        return null;
    }

    @Override
    public String toString() {
        return "LayoutedComponent{" +
                "component=" + component +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    public String getIcon() {
        if (component instanceof Item) {
            return ((Item) component).getLabel(Label._icondata);
        }

        return null;
    }

    public String getColor() {
        if (StringUtils.hasLength(component.getColor())) {
            return component.getColor();
        }
        return defaultColor;
    }

    public void setDefaultColor(String color) {
        this.defaultColor =color;
    }
}
