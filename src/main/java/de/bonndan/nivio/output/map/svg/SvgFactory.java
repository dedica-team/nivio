package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.GroupMapItem;
import de.bonndan.nivio.output.map.ItemMapItem;
import de.bonndan.nivio.output.map.RenderedXYMap;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class SvgFactory extends Component {

    private static final Logger LOGGER = LoggerFactory.getLogger(SvgFactory.class);

    public static final int LABEL_WIDTH = 200;
    private static int ICON_SIZE = 40;
    private int padding = 10;
    private Map<String, ItemMapItem> itemMapItembyFQI = new HashMap<>();
    private List<Hex> occupied = new ArrayList<>();
    private final RenderedXYMap map;
    private boolean debug = false;

    public SvgFactory(RenderedXYMap map) {
        this.map = map;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public DomContent render() {

        Map<ItemMapItem, Hex> vertexHexes = new HashMap<>();
        String css = "";
        try {
            InputStream resourceAsStream = getClass().getResourceAsStream("/static/css/svg.css");
            css = new String(StreamUtils.copyToByteArray(resourceAsStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        final HexFactory hexFactory = new HexFactory();

        map.items.forEach(itemMapItem -> {
            var hex = hexFactory.of(itemMapItem.x, itemMapItem.y);
            hex.id = itemMapItem.id;
            vertexHexes.put(itemMapItem, hex);
            itemMapItembyFQI.put(itemMapItem.id, itemMapItem);
            occupied.add(hex);
        });

        var pathFinder = new PathFinder(occupied);
        pathFinder.debug = this.debug;
        UnescapedText style = rawHtml("<style>\n" + css + "</style>");

        AtomicInteger width = new AtomicInteger(0);
        AtomicInteger height = new AtomicInteger(0);
        List<DomContent> groups = map.groups.stream().map(group -> {
            SVGGroup SVGGroup = getGroup(hexFactory, group, itemMapItembyFQI);
            if ((SVGGroup.x + SVGGroup.width) > width.get())
                width.set((int) (SVGGroup.x + SVGGroup.width));
            if ((SVGGroup.y + SVGGroup.height) > height.get())
                height.set((int) (SVGGroup.y + SVGGroup.height));
            return SVGGroup.render();
        }).collect(Collectors.toList());


        return
                SvgTagCreator.svg(style)
                        .attr("version", "1.1")
                        .attr("xmlns", "http://www.w3.org/2000/svg")
                        .attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
                        .attr("width", width.addAndGet(ICON_SIZE + LABEL_WIDTH))
                        .attr("height", height.addAndGet(ICON_SIZE))

                        //groups
                        .with(groups)

                        //relations
                        .with(
                                map.items.stream().flatMap(vertex -> {
                                            LOGGER.debug("Adding {} relations for {}", vertex.relations.size(), vertex.id);
                                            return vertex.relations.stream().map(rel -> {
                                                Hex start = vertexHexes.get(vertex);
                                                Hex target = vertexHexes.get(itemMapItembyFQI.get(rel.target.getFullyQualifiedIdentifier().toString()));
                                                HexPath bestPath = pathFinder.getPath(start, target);
                                                if (bestPath != null) {
                                                    SVGRelation SVGRelation = new SVGRelation(bestPath, vertex.color, rel);
                                                    return SVGRelation.render();
                                                }
                                                return null;
                                            });
                                        }
                                ).collect(Collectors.toList())
                        )


                        //items
                        .with(
                                map.items.stream().map(vertex -> {

                                    var fill = "";
                                    if (!StringUtils.isEmpty(vertex.image)) {
                                        fill = Base64.getEncoder().encodeToString(vertex.id.getBytes());
                                    }
                                    SVGItemLabel label = new SVGItemLabel(vertex, LABEL_WIDTH, ICON_SIZE, padding);
                                    Point2D.Double pos = vertexHexes.get(vertex).toPixel();
                                    SVGItem SVGItem = new SVGItem(label.render(), vertex, pos, fill, "stroke: #" + vertex.color);
                                    return SVGItem.render();
                                }).collect(Collectors.toList())
                        )

                        .with(
                                map.items.stream()
                                        .filter(vertex -> !StringUtils.isEmpty(vertex.image))
                                        .map(vertex -> {
                                            var fill = vertex.image;
                                            var id = Base64.getEncoder().encodeToString(vertex.id.getBytes());
                                            SVGPattern SVGPattern = new SVGPattern(id, fill, ICON_SIZE - padding, padding);
                                            return SVGPattern.render();
                                        }).collect(Collectors.toList())
                        );


    }

    private SVGGroup getGroup(HexFactory hexFactory, GroupMapItem group, Map<String, ItemMapItem> byFQI) {

        Set<ItemMapItem> groupMapItems = group.group.getItems().stream()
                .map(item -> byFQI.get(item.getFullyQualifiedIdentifier().toString()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        AtomicLong minX = new AtomicLong(Long.MAX_VALUE);
        AtomicLong maxX = new AtomicLong(Long.MIN_VALUE);
        AtomicLong minY = new AtomicLong(Long.MAX_VALUE);
        AtomicLong maxY = new AtomicLong(Long.MIN_VALUE);

        groupMapItems.forEach(itemMapItem -> {
            Point2D.Double p = hexFactory.of(itemMapItem.x, itemMapItem.y).toPixel();
            if (p.x < minX.get()) minX.set((long) p.x);
            if (p.x > maxX.get()) maxX.set((long) p.x);
            if (p.y < minY.get()) minY.set((long) p.y);
            if (p.y > maxY.get()) maxY.set((long) p.y);
        });

        var padding = 50;
        var startPoint = new Point2D.Double(minX.get() - padding, minY.get() - padding);
        var endPoint = new Point2D.Double(maxX.get() + padding, maxY.get() + padding);

        int width = (int) (endPoint.x - startPoint.x);
        int height = (int) (endPoint.y - startPoint.y);

        return new SVGGroup(group, startPoint.x, startPoint.y, width, height);
    }

    public String getXML() {
        return "<?xml version=\"1.0\" standalone=\"yes\"?>" + render().render();
    }
}

