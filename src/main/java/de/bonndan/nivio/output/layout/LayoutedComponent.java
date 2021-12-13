package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A value object to hold dimensions and position data of rendered {@link Component}s.
 *
 * Also holds children to allow recursive operations.
 */
public class LayoutedComponent {

    private final Component component;

    private long x = 0;
    private long y = 0;
    public double width = 50;
    public double height = 50;
    public static double padding = 50;
    private final List<Component> opposites;
    private List<LayoutedComponent> children = new ArrayList<>();

    @Nullable
    private String defaultColor;

    public static LayoutedComponent from(@NonNull final Component parent, @NonNull final List<LayoutedComponent> children) {

        if (Objects.requireNonNull(children).isEmpty()) {
            throw new IllegalArgumentException("Children must not be empty.");
        }

        LayoutedComponent layoutedComponent = new LayoutedComponent(parent, children, new ArrayList<>());

        if (children.size() == 1) {
            layoutedComponent.setWidth(children.get(0).getWidth());
            layoutedComponent.setHeight(children.get(0).getHeight());
        } else {
            var minX = new AtomicLong(Integer.MAX_VALUE);
            var maxX = new AtomicLong(Integer.MIN_VALUE);
            var minY = new AtomicLong(Integer.MAX_VALUE);
            var maxY = new AtomicLong(Integer.MIN_VALUE);

            for (LayoutedComponent b : children) {
                if (b.x < minX.get()) minX.set(b.x);
                if (b.x > maxX.get()) maxX.set(b.x);
                if (b.y < minY.get()) minY.set(b.y);
                if (b.y > maxY.get()) maxY.set(b.y);
            }

            layoutedComponent.setWidth((double) maxX.get() - minX.get() + padding);
            layoutedComponent.setHeight((double) maxY.get() - minY.get() + padding);
            layoutedComponent.x = minX.get();
            layoutedComponent.y = minY.get();
        }

        return layoutedComponent;
    }

    public LayoutedComponent(@NonNull final Component component, @NonNull final List<LayoutedComponent> children, List<Component> opposites) {
        this.component = Objects.requireNonNull(component);
        this.children = Objects.requireNonNull(children);
        this.opposites = opposites;
    }

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
        if (width == 0D) {
            throw new IllegalArgumentException("Width cannot be set to zero");
        }
        this.width = width;
    }

    public double getWidth() {
        return width;
    }

    public void setHeight(double height) {
        if (height == 0D) {
            throw new IllegalArgumentException("Height cannot be set to zero");
        }
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
        this.defaultColor = color;
    }

    /**
     * Calculates the radius based on the farthest child from the center.
     *
     * Includes the child's radius, not just it's center.
     *
     * @return radius
     */
    public double getRadius() {
        final var center = getCenter();


        Optional<Double> farthest = children.stream().reduce((component1, component2) -> {
            Point2D.Double center1 = component1.getCenter();
            Point2D.Double center2 = component2.getCenter();
            var dist1 = Geometry.getDistance(center1.x - center.x, center1.y - center.y) + component1.getRadius();
            var dist2 = Geometry.getDistance(center2.x - center.x, center2.y - center.y) + component2.getRadius();
            if (dist1 > dist2)
                return component1;
            return component2;
        }).map(component1 -> {
            Point2D.Double center1 = component1.getCenter();
            return Geometry.getDistance(center1.x - center.x, center1.y - center.y) + component1.getRadius();
        });
        return farthest.orElse(Math.max(width / 2, height / 2));
    }

    /**
     * Returns the center coordinate regarding x,y,width and height.
     */
    public Point2D.Double getCenter() {
        return new Point2D.Double(x + width / 2.0, y + height / 2.0);
    }

    /**
     * @return top-left and bottom-right as points
     */
    public List<Point2D.Double> getMinMaxBoundaries() {
        Point2D.Double center = getCenter();
        double radius = getRadius();
        return List.of(
                new Point2D.Double(center.x - radius, center.y - radius),
                new Point2D.Double(center.x + radius, center.y + radius)
        );
    }
}
