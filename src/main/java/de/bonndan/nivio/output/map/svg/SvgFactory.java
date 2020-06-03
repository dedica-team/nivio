package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.output.Rendered;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.HexFactory;
import de.bonndan.nivio.output.map.hex.PathFinder;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static de.bonndan.nivio.output.map.MapFactory.DEFAULT_ICON_SIZE;
import static de.bonndan.nivio.output.map.svg.SVGItemLabel.LABEL_WIDTH;
import static j2html.TagCreator.rawHtml;

/**
 * Creates an SVG document based on pre-rendered map items.
 */
public class SvgFactory extends Component {

    private static final Logger LOGGER = LoggerFactory.getLogger(SvgFactory.class);

    private final Set<Hex> occupied = new HashSet<>();
    private final LandscapeImpl landscape;
    private final MapStyleSheetFactory mapStyleSheetFactory;
    private boolean debug = false;

    public SvgFactory(LandscapeImpl landscape, MapStyleSheetFactory mapStyleSheetFactory) {
        this.landscape = landscape;
        this.mapStyleSheetFactory = mapStyleSheetFactory;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public DomContent render() {

        Map<LandscapeItem, Hex> vertexHexes = new HashMap<>();
        final HexFactory hexFactory = new HexFactory();

        landscape.getItems().all().forEach(item -> {
            Hex hex = null;
            int i = 0;
            while (hex == null || occupied.contains(hex)) {
                hex = hexFactory.of(item.getX() - i, item.getY() - i);
                i++;
            }

            hex.id = item.getFullyQualifiedIdentifier().toString();
            vertexHexes.put(item, hex);
            occupied.add(hex);
        });

        var pathFinder = new PathFinder(occupied);
        pathFinder.debug = this.debug;

        DomContent title = SvgTagCreator.text(landscape.getName())
                .attr("x", LABEL_WIDTH + 10)
                .attr("y", -LABEL_WIDTH / 2 + 20)
                .attr("class", "title");
        DomContent logo = null;
        String logoUrl = landscape.getConfig().getBranding().getMapLogo();
        if (!StringUtils.isEmpty(logoUrl)) {
            logo = SvgTagCreator.image()
                    .attr("xlink:href", logoUrl)
                    .attr("y", -LABEL_WIDTH)
                    .attr("width", LABEL_WIDTH)
                    .attr("height", LABEL_WIDTH);
        }

        AtomicInteger width = new AtomicInteger(0);
        AtomicInteger height = new AtomicInteger(0);

        List<DomContent> groups = landscape.getGroups().values().stream().map(group -> {
            SVGGroup SVGGroup = getGroup(hexFactory, (Group) group);
            if ((SVGGroup.x + SVGGroup.width) > width.get())
                width.set((int) (SVGGroup.x + SVGGroup.width));
            if ((SVGGroup.y + SVGGroup.height) > height.get())
                height.set((int) (SVGGroup.y + SVGGroup.height));
            return SVGGroup.render();
        }).collect(Collectors.toList());

        List<DomContent> patterns = landscape.getItems().all().stream()
                .filter(item -> !StringUtils.isEmpty(item.getFill()))
                .map(item -> {
                    SVGPattern SVGPattern = new SVGPattern(item.getFill());
                    return SVGPattern.render();
                }).collect(Collectors.toList());

        List<DomContent> items = landscape.getItems().all().stream().map(item -> {
            SVGItemLabel label = new SVGItemLabel(item);
            Point2D.Double pos = vertexHexes.get(item).toPixel();
            SVGItem SVGItem = new SVGItem(label.render(), item, pos);
            return SVGItem.render();
        }).collect(Collectors.toList());

        List<DomContent> relations = landscape.getItems().all().stream().flatMap(item -> {
                    LOGGER.debug("Adding {} relations for {}", item.getRelations().size(), item.getFullyQualifiedIdentifier());
                    return item.getRelations().stream()
                            .filter(rel -> rel.getSource().equals(item)) //do not paint twice / incoming (inverse) relations
                            .map(rel -> {
                                Hex start = vertexHexes.get(item);
                                Hex target = vertexHexes.get(rel.getTarget());
                                HexPath bestPath = pathFinder.getPath(start, target);
                                if (bestPath != null) {
                                    SVGRelation SVGRelation = new SVGRelation(bestPath, item.getColor(), rel);
                                    LOGGER.debug("Added path for item {} relation {} -> {}", item, rel.getSource(), rel.getTarget());
                                    return SVGRelation.render();
                                }
                                LOGGER.warn("No path found for item {} relation {}", item, rel);
                                return null;
                            });
                }
        ).collect(Collectors.toList());

        UnescapedText style = rawHtml("<style>\n" + getStyles() + "</style>");

        return
                SvgTagCreator.svg(style)
                        .attr("version", "1.1")
                        .attr("xmlns", "http://www.w3.org/2000/svg")
                        .attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
                        .attr("width", width.addAndGet(DEFAULT_ICON_SIZE + LABEL_WIDTH / 2))
                        .attr("height", height.addAndGet(DEFAULT_ICON_SIZE))
                        .attr("viewBox", "0 -" + LABEL_WIDTH + " " + (width.get() + LABEL_WIDTH) + " " + (height.get() + LABEL_WIDTH))

                        .with(logo, title)
                        .with(groups)
                        .with(relations)
                        .with(items)
                        .with(SvgTagCreator.defs().with(patterns));
    }

    private String getStyles() {
        String css = "";
        try {
            InputStream resourceAsStream = getClass().getResourceAsStream("/static/css/svg.css");
            css = new String(StreamUtils.copyToByteArray(resourceAsStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return css + "\n" + mapStyleSheetFactory.getMapStylesheet(landscape.getConfig(), landscape.getLog());
    }

    private SVGGroup getGroup(HexFactory hexFactory, Group group) {

        List<Item> items = group.getItems();
        AtomicLong minX = new AtomicLong(Long.MAX_VALUE);
        AtomicLong maxX = new AtomicLong(Long.MIN_VALUE);
        AtomicLong minY = new AtomicLong(Long.MAX_VALUE);
        AtomicLong maxY = new AtomicLong(Long.MIN_VALUE);

        items.forEach(item -> {
            Point2D.Double p = hexFactory.of(Integer.parseInt(item.getLabel(Rendered.LX)), Integer.parseInt(item.getLabel(Rendered.LY))).toPixel();
            if (p.x < minX.get()) minX.set((long) p.x);
            if (p.x > maxX.get()) maxX.set((long) p.x);
            if (p.y < minY.get()) minY.set((long) p.y);
            if (p.y > maxY.get()) maxY.set((long) p.y);
        });

        var padding = DEFAULT_ICON_SIZE;
        var startPoint = new Point2D.Double(minX.get() - padding, minY.get() - padding);
        var endPoint = new Point2D.Double(maxX.get() + padding, maxY.get() + padding);

        int width = (int) (endPoint.x - startPoint.x) + 2 * padding;
        int height = (int) (endPoint.y - startPoint.y) + 2 * padding;

        return new SVGGroup(group, startPoint.x - padding / 2, startPoint.y - padding / 2, width, height);
    }

    public String getXML() {
        return render().render();
    }
}

