package de.bonndan.nivio.output;

import de.bonndan.nivio.model.Labeled;

/**
 * A landscape component that has been rendered.
 *
 *
 */
public interface Rendered extends Labeled {

    String LABEL_PREFIX_RENDERED = "nivio.rendered.";
    String LABEL_RENDERED_COLOR = LABEL_PREFIX_RENDERED + "color";
    String LY = LABEL_PREFIX_RENDERED + "y";
    String LX = LABEL_PREFIX_RENDERED + "x";
    String LABEL_RENDERED_ICON = LABEL_PREFIX_RENDERED + "icon";
    String LABEL_FILL = "fill";
    String LABEL_RENDERED_WIDTH = LABEL_PREFIX_RENDERED + "width";
    String LABEL_RENDERED_HEIGHT = LABEL_PREFIX_RENDERED + "height";

    default void setWidth(Long width) {
        setLabel(LABEL_RENDERED_WIDTH, String.valueOf(width));
    }

    default Long getWidth() {
        String width = getLabel(LABEL_RENDERED_WIDTH);
        return width == null ? null : Long.parseLong(width);
    }

    default void setHeight(Long height) {
        setLabel(LABEL_RENDERED_HEIGHT, String.valueOf(height));
    }

    default Long getHeight() {
        String height = getLabel(LABEL_RENDERED_HEIGHT);
        return height == null ? null : Long.parseLong(height);
    }

    default void setColor(String color) {
        setLabel(LABEL_RENDERED_COLOR, color);
    }

    default String getColor() {
        return getLabel(LABEL_RENDERED_COLOR);
    }

    default void setFill(String fill) {
        setLabel(LABEL_FILL, fill);
    }

    default String getFill() {
        return getLabel(LABEL_FILL);
    }


    default void setX(Long x) {
        setLabel(LX, String.valueOf(x));
    }

    default Long getX() {
        String x = getLabel(LX);
        return x == null ? null : Long.parseLong(x);
    }

    default void setY(Long y) {
        setLabel(LY, String.valueOf(y));
    }

    default Long getY() {
        String y = getLabel(LY);
        return y == null ? null : Long.parseLong(y);
    }

}
