package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.Rendered;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.HexFactory;
import de.bonndan.nivio.output.map.hex.PathFinder;
import de.bonndan.nivio.util.URLHelper;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static de.bonndan.nivio.output.map.svg.SVGItemLabel.LABEL_WIDTH;
import static j2html.TagCreator.*;

public class SvgFactory extends Component {

    private static final Logger LOGGER = LoggerFactory.getLogger(SvgFactory.class);

    static int ICON_SIZE = 40;
    private int padding = 10;
    private List<Hex> occupied = new ArrayList<>();
    private final LandscapeImpl landscape;
    private final LandscapeConfig landscapeConfig;
    private boolean debug = false;

    public SvgFactory(LandscapeImpl landscape, LandscapeConfig landscapeConfig) {
        this.landscape = landscape;
        this.landscapeConfig = landscapeConfig;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public DomContent render() {

        Map<LandscapeItem, Hex> vertexHexes = new HashMap<>();

        String css = "";
        try {
            InputStream resourceAsStream = getClass().getResourceAsStream("/static/css/svg.css");
            css = new String(StreamUtils.copyToByteArray(resourceAsStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (landscapeConfig.getBranding().getMapStylesheet() != null) {

            //TODO hackish
            FileFetcher fileFetcher = new FileFetcher(new HttpService());

            try {
                URL url = new URL(landscapeConfig.getBranding().getMapStylesheet());
                String mapCss;
                if (URLHelper.isLocal(url)) {
                    mapCss = Files.readString(new File(url.toString()).toPath());
                } else {
                    mapCss = fileFetcher.get(url);
                }
                css = css + "\n" + mapCss;
            } catch (IOException e) {
                LOGGER.warn("Failed to load customer stylesheet {}", landscapeConfig.getBranding().getMapStylesheet(), e);
            }
        }

        final HexFactory hexFactory = new HexFactory();

        landscape.getItems().all().forEach(item -> {
            var hex = hexFactory.of(item.getX(), item.getY());
            hex.id = item.getFullyQualifiedIdentifier().toString();
            vertexHexes.put(item, hex);
            occupied.add(hex);
        });

        var pathFinder = new PathFinder(occupied);
        pathFinder.debug = this.debug;
        UnescapedText style = rawHtml("<style>\n" + css + "</style>");

        AtomicInteger width = new AtomicInteger(0);
        AtomicInteger height = new AtomicInteger(0);


        DomContent title = SvgTagCreator.text(landscape.getName())
                .attr("x", LABEL_WIDTH + 10)
                .attr("y", -LABEL_WIDTH / 2 + 20)
                .attr("class", "title");
        DomContent logo = null;
        String logoUrl = landscapeConfig.getBranding().getMapLogo();
        if (!StringUtils.isEmpty(logoUrl)) {
            logo = SvgTagCreator.image()
                    .attr("xlink:href", logoUrl)
                    .attr("y", -LABEL_WIDTH)
                    .attr("width", LABEL_WIDTH)
                    .attr("height", LABEL_WIDTH);
        }

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
                    SVGPattern SVGPattern = new SVGPattern(item.getFill(), ICON_SIZE);
                    return SVGPattern.render();
                }).collect(Collectors.toList());

        List<DomContent> items = landscape.getItems().all().stream().map(item -> {
            SVGItemLabel label = new SVGItemLabel(item, ICON_SIZE, padding);
            Point2D.Double pos = vertexHexes.get(item).toPixel();
            SVGItem SVGItem = new SVGItem(label.render(), item, pos);
            return SVGItem.render();
        }).collect(Collectors.toList());

        List<DomContent> relations = landscape.getItems().all().stream().flatMap(item -> {
                    LOGGER.debug("Adding {} relations for {}", item.getRelations().size(), item.getFullyQualifiedIdentifier());
                    return item.getRelations().stream().map(rel -> {
                        Hex start = vertexHexes.get(item);
                        Hex target = vertexHexes.get((Item) rel.getTarget());
                        HexPath bestPath = pathFinder.getPath(start, target);
                        if (bestPath != null) {
                            SVGRelation SVGRelation = new SVGRelation(bestPath, item.getColor(), rel);
                            return SVGRelation.render();
                        }
                        return null;
                    });
                }
        ).collect(Collectors.toList());

        return
                SvgTagCreator.svg(style)
                        .attr("version", "1.1")
                        .attr("xmlns", "http://www.w3.org/2000/svg")
                        .attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
                        .attr("width", width.addAndGet(ICON_SIZE + LABEL_WIDTH / 2))
                        .attr("height", height.addAndGet(ICON_SIZE))
                        .attr("viewBox", "0 -" + LABEL_WIDTH + " " + (width.get() + LABEL_WIDTH) + " " + (height.get() + LABEL_WIDTH))

                        .with(logo, title)
                        .with(groups)
                        .with(relations)
                        .with(items)
                        .with(SvgTagCreator.defs().with(patterns));
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

        var padding = 50;
        var startPoint = new Point2D.Double(minX.get() - padding, minY.get() - padding);
        var endPoint = new Point2D.Double(maxX.get() + padding, maxY.get() + padding);

        int width = (int) (endPoint.x - startPoint.x) + padding;
        int height = (int) (endPoint.y - startPoint.y) + padding;

        return new SVGGroup(group, startPoint.x - padding / 2, startPoint.y - padding / 2, width, height);
    }

    public String getXML() {
        return render().render();
    }
}

