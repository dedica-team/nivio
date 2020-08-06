package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.Renderer;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.hex.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Turns the layouted landscape into a SVG image.
 *
 *
 */
@Service
public class SVGRenderer implements Renderer<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SVGRenderer.class);
    public static final int DEFAULT_ICON_SIZE = 50;

    private final MapStyleSheetFactory mapStyleSheetFactory;

    public SVGRenderer(MapStyleSheetFactory mapStyleSheetFactory) {
        this.mapStyleSheetFactory = mapStyleSheetFactory;
    }

    @Override
    public String render(LayoutedComponent layoutedComponent) {
        applyValues(layoutedComponent);
        SVGDocument svgDocument = new SVGDocument(layoutedComponent, getStyles((LandscapeImpl) layoutedComponent.getComponent()));
        return svgDocument.getXML();
    }

    @Override
    public void render(LayoutedComponent landscape, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(render(landscape));
        fileWriter.close();
    }

    private String getStyles(LandscapeImpl landscape) {
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

            LOGGER.debug("group offset {} {}", groupBounds.getX(), groupBounds.getY());
            Group group = (Group) groupBounds.getComponent();
            groupBounds.setX(groupBounds.getX() + margin.x);
            groupBounds.setY(groupBounds.getY() + margin.y);

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
    private Point2D.Double getMargins(LayoutedComponent layoutedComponent) {
        AtomicLong minX = new AtomicLong(0);
        AtomicLong maxX = new AtomicLong(0);
        AtomicLong minY = new AtomicLong(0);
        AtomicLong maxY = new AtomicLong(0);
        layoutedComponent.getChildren().forEach(g -> {
            if (g.getX() < minX.get())
                minX.set((long) g.getX());
            if (g.getX() > maxX.get())
                maxX.set((long) g.getX());
            if (g.getY() < minY.get())
                minY.set((long) g.getY());
            if (g.getY() > maxY.get())
                maxY.set((long) g.getY());
        });

        layoutedComponent.setWidth(maxX.get() - minX.get());
        layoutedComponent.setHeight(maxY.get() - minY.get());

        int marginX = 2 * Hex.HEX_SIZE;
        if (minX.get() < 0) {
            marginX += minX.get() * -1;
        }

        int marginY = 2 * Hex.HEX_SIZE;
        if (minY.get() < 0) {
            marginY += minY.get() * -1;
        }

        LOGGER.debug("Map shift x {} y {} ", marginX, marginY);
        return new Point2D.Double(marginX, marginY);
    }
}
