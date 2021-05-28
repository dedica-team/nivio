package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.Renderer;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.hex.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Turns the layouted landscape into a SVG image.
 */
@Service
public class SVGRenderer implements Renderer<SVGDocument> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SVGRenderer.class);

    public static final int DEFAULT_ICON_SIZE = 50;

    private final MapStyleSheetFactory mapStyleSheetFactory;

    public SVGRenderer(MapStyleSheetFactory mapStyleSheetFactory) {
        this.mapStyleSheetFactory = mapStyleSheetFactory;
    }

    @Override
    public SVGDocument render(@NonNull final LayoutedComponent landscape, @Nullable final Assessment assessment, boolean debug) {
        applyValues(Objects.requireNonNull(landscape));
        SVGDocument svgDocument = new SVGDocument(landscape, assessment, getStyles((Landscape) landscape.getComponent()));
        svgDocument.setDebug(debug);
        return svgDocument;
    }

    @Override
    public void render(@NonNull final LayoutedComponent landscape, @NonNull final Assessment assessment, @NonNull final File file, boolean debug) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(render(landscape, assessment, debug).getXML());
        } catch (IOException e) {
            LOGGER.error("Failed to render to file", e);
        }
    }

    private String getStyles(Landscape landscape) {
        String css = "";
        try (InputStream resourceAsStream = getClass().getResourceAsStream("/static/css/svg.css")) {
            css = new String(StreamUtils.copyToByteArray(resourceAsStream));
        } catch (IOException e) {
            LOGGER.error("Failed to load stylesheet /static/css/svg.css");
        }

        return css + "\n" + mapStyleSheetFactory.getMapStylesheet(landscape.getConfig(), landscape.getLog());
    }

    /**
     * @param layoutedComponent layouted landscape
     */
    private void applyValues(LayoutedComponent layoutedComponent) {

        Point2D.Double margin = getMargins(layoutedComponent);
        layoutedComponent.getChildren().forEach(groupBounds -> {

            LOGGER.debug("group {} offset {} {}", groupBounds.getComponent().getIdentifier(), groupBounds.getX(), groupBounds.getY());
            groupBounds.setX(groupBounds.getX() + margin.x);
            groupBounds.setY(groupBounds.getY() + margin.y);
            LOGGER.debug("corrected group {} offset {} {}", groupBounds.getComponent().getIdentifier(), groupBounds.getX(), groupBounds.getY());

            groupBounds.getChildren().forEach(itemBounds -> {
                LOGGER.debug("original item pos {} {}", itemBounds.getX(), itemBounds.getY());
                itemBounds.setX(itemBounds.getX() + groupBounds.getX());
                itemBounds.setY(itemBounds.getY() + groupBounds.getY());
                LOGGER.debug("item pos with group offset: {} {}", itemBounds.getX(), itemBounds.getY());
            });
        });
    }

    /**
     * @return the left/top extra margin to shift all items into positive coordinates
     */
    private Point2D.Double getMargins(LayoutedComponent layoutedLandscape) {
        List<Point2D.Double> minMaxBoundaries = getMinMaxBoundaries(layoutedLandscape);
        var min = minMaxBoundaries.get(0);
        var max = minMaxBoundaries.get(1);

        layoutedLandscape.setWidth(max.x - min.x);
        layoutedLandscape.setHeight(max.y - min.y);

        int marginX = 2 * Hex.HEX_SIZE;
        int marginY = 2 * Hex.HEX_SIZE;

        if (min.x < 0) {
            LOGGER.debug("fixing minX by {}", min.x * -1);
            marginX += min.x * -1;
        }

        if (min.y < 0) {
            LOGGER.debug("fixing minY by {}", min.y * -1);
            marginY += min.y * -1;
        }

        LOGGER.debug("Map shift x {} y {} ", marginX, marginY);
        return new Point2D.Double(marginX, marginY);
    }

    /**
     * @param layoutedComponent parent
     * @return top-left and bottom-right as points
     */
    static List<Point2D.Double> getMinMaxBoundaries(LayoutedComponent layoutedComponent) {
        AtomicLong minX = new AtomicLong(Integer.MAX_VALUE);
        AtomicLong maxX = new AtomicLong(Integer.MIN_VALUE);
        AtomicLong minY = new AtomicLong(Integer.MAX_VALUE);
        AtomicLong maxY = new AtomicLong(Integer.MIN_VALUE);

        layoutedComponent.getChildren().forEach(c -> {
            double x = c.getX();
            double y = c.getY();

            if (x < minX.get())
                minX.set((long) x);
            if (x > maxX.get())
                maxX.set((long) x);

            if (y < minY.get())
                minY.set((long) y);
            if (y > maxY.get())
                maxY.set((long) y);
        });

        return List.of(
                new Point2D.Double(minX.get(), minY.get()),
                new Point2D.Double(maxX.get(), maxY.get())
        );
    }
}
