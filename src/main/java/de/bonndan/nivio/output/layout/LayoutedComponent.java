package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.output.map.hex.Hex;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A value object to hold dimensions and position data of rendered {@link Component}s.
 *
 * Also holds children to allow recursive operations.
 */
public class LayoutedComponent {

    private static final double PADDING = 50;

    private final Component component;

    private long centerX = 0;
    private long centerY = 0;
    private final double width;
    private final double height;
    private final List<Component> opposites;
    private List<LayoutedComponent> children = new ArrayList<>();

    @Nullable
    private String defaultColor;

    public static LayoutedComponent from(@NonNull final Component parent, @NonNull final List<LayoutedComponent> children) {

        if (Objects.requireNonNull(children).isEmpty()) {
            throw new IllegalArgumentException("Children must not be empty.");
        }

        DimAndCenter dimAndCenter = getDimAndCenter(children);
        LayoutedComponent layoutedComponent = new LayoutedComponent(parent, children, new ArrayList<>(), dimAndCenter.width, dimAndCenter.height);
        layoutedComponent.setCenterX((long) dimAndCenter.x);
        layoutedComponent.setCenterY((long) dimAndCenter.y);

        return layoutedComponent;
    }

    static DimAndCenter getDimAndCenter(@NonNull final List<LayoutedComponent> children) {

        double calcWidth;
        double calcHeight;
        double x;
        double y;

        if (children.size() == 1) {
            LayoutedComponent child = children.get(0);
            calcWidth = child.getWidth();
            calcHeight = child.getHeight();
            x = child.centerX;
            y = child.centerY;
            return new DimAndCenter(calcWidth, calcHeight, x, y);
        }

        var minX = new AtomicLong(Integer.MAX_VALUE);
        var maxX = new AtomicLong(Integer.MIN_VALUE);
        var minY = new AtomicLong(Integer.MAX_VALUE);
        var maxY = new AtomicLong(Integer.MIN_VALUE);

        for (LayoutedComponent child : children) {
            if (child.centerX < minX.get()) minX.set(child.centerX);
            if (child.centerX > maxX.get()) maxX.set(child.centerX);
            if (child.centerY < minY.get()) minY.set(child.centerY);
            if (child.centerY > maxY.get()) maxY.set(child.centerY);
        }

        calcWidth = (double) maxX.get() - minX.get() + PADDING;
        calcHeight = (double) maxY.get() - minY.get() + PADDING;
        x = minX.get() + (maxX.get() - minX.get()) / 2D;
        y = minY.get() + (maxY.get() - minY.get()) / 2D;

        return new DimAndCenter(calcWidth, calcHeight, x, y);
    }

    public LayoutedComponent(@NonNull final Component component,
                             @NonNull final List<LayoutedComponent> children,
                             List<Component> opposites,
                             double width,
                             double height
    ) {
        this.component = Objects.requireNonNull(component);
        this.children = Objects.requireNonNull(children);
        this.opposites = opposites;
        this.width = width;
        this.height = height;
    }

    public LayoutedComponent(@NonNull final Component component, List<Component> opposites) {
        this.component = Objects.requireNonNull(component);
        this.opposites = opposites;
        this.width = 50;
        this.height = 50;
    }

    LayoutedComponent(Component component, double width, double height) {
        this(component, new ArrayList<>(), new ArrayList<>(), width, height);
    }

    /**
     * center coordinate
     */
    public long getCenterX() {
        return centerX;
    }

    /**
     * center coordinate
     */
    public long getCenterY() {
        return centerY;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setCenterX(long x) {
        this.centerX = x;
    }

    public void setCenterY(long y) {
        this.centerY = y;
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
            return component.getLabel(Label._filldata);
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
            return component.getLabel(Label._icondata);
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
        return Geometry.getDistance(centerX - (centerX - width / 2), centerY - (centerY - height / 2)) + 2 * Hex.HEX_SIZE;
    }

    /**
     * Returns the center coordinate regarding x,y,width and height.
     */
    public Point2D.Double getCenter() {
        return new Point2D.Double(centerX, centerY);
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

    static class DimAndCenter {
        final double width;
        final double height;
        final double x;
        final double y;

        public DimAndCenter(double width, double height, double x, double y) {
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
        }
    }
}
