package de.bonndan.nivio.output.map.svg;

import j2html.tags.DomContent;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.Objects;

public abstract class Component {

    public abstract DomContent render();

    /**
     * Applies an offset to cartesian coordinates.
     *
     * See https://github.com/dedica-team/nivio/issues/438
     *
     * @param offset x and y offset
     * @return self
     */
    public final Component shift(@NonNull final Point2D.Double offset) {
        applyShift(Objects.requireNonNull(offset));
        return this;
    }

    /**
     * Override this method to apply the shifting offset.
     *
     * @param offset x,y
     */
    protected void applyShift(Point2D.Double offset) {

    }

    public static double round(Float f) {
        return Math.round(f * 100.0) / 100.0;
    }

    public static double round(Double d) {
        return Math.round(d * 100.0) / 100.0;
    }
}
