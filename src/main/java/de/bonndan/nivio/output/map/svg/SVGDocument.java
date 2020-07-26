package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.layout.LayoutedComponent;
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
import java.util.stream.Collectors;

import static de.bonndan.nivio.output.map.svg.SVGItemLabel.LABEL_WIDTH;
import static de.bonndan.nivio.output.map.svg.SVGRenderer.DEFAULT_ICON_SIZE;
import static j2html.TagCreator.rawHtml;


/**
 * Creates an SVG document based on pre-rendered map items.
 */
public class SVGDocument extends Component {

    private static final Logger LOGGER = LoggerFactory.getLogger(SVGDocument.class);

    private final Set<Hex> occupied = new HashSet<>();
    private final LayoutedComponent layouted;
    private final MapStyleSheetFactory mapStyleSheetFactory;
    private final LandscapeImpl landscape;
    private boolean debug = false;

    public SVGDocument(LayoutedComponent layouted, MapStyleSheetFactory mapStyleSheetFactory) {
        this.layouted = layouted;
        this.landscape = (LandscapeImpl) layouted.getComponent();
        this.mapStyleSheetFactory = mapStyleSheetFactory;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public DomContent render() {

        Map<LandscapeItem, Hex> vertexHexes = new HashMap<>();
        final HexFactory hexFactory = new HexFactory();

        List<DomContent> patterns = new ArrayList<>();
        List<DomContent> items = new ArrayList<>();
        AtomicInteger width = new AtomicInteger(0);
        AtomicInteger height = new AtomicInteger(0);

        //transform all item positions to hex map postions
        layouted.getChildren().forEach(group -> {
            group.getChildren().forEach(layoutedItem -> {
                Hex hex = null;
                int i = 0;
                while (hex == null || occupied.contains(hex)) {
                    hex = hexFactory.of(Math.round(layoutedItem.getX()) - i, Math.round(layoutedItem.getY()) - i);
                    i++;
                }

                Item item = (Item) layoutedItem.getComponent();
                hex.id = item.getFullyQualifiedIdentifier().jsonValue();
                vertexHexes.put(item, hex);
                occupied.add(hex);

                //collect patterns for icons
                if (!StringUtils.isEmpty(layoutedItem.getFill())) {
                    SVGPattern SVGPattern = new SVGPattern(layoutedItem.getFill());
                    patterns.add(SVGPattern.render());
                }

                //render icons
                SVGItemLabel label = new SVGItemLabel(item);
                Point2D.Double pos = vertexHexes.get(item).toPixel();

                //iterate all items to render them and collect max svg dimension
                // add extra margins size group area is larger than max item positions
                if ((pos.x + 3 * Hex.HEX_SIZE) > width.get())
                    width.set((int) (pos.x + 3 * Hex.HEX_SIZE));
                if ((pos.y + 3 * Hex.HEX_SIZE) > height.get())
                    height.set((int) (pos.y + 3 * Hex.HEX_SIZE));

                SVGItem SVGItem = new SVGItem(label.render(), layoutedItem, pos);
                items.add(SVGItem.render());
            });
        });


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


        // find and render relations
        var pathFinder = new PathFinder(occupied);
        pathFinder.debug = this.debug;
        List<SVGRelation> relations = getRelations(layouted, vertexHexes, pathFinder);

        //generate group areas
        List<DomContent> groups = layouted.getChildren().stream().map(groupLayout -> {
            SVGGroupArea area = SVGGroupAreaFactory.getGroup(occupied, (Group) groupLayout.getComponent(), vertexHexes, relations);
            return area.render();
        }).collect(Collectors.toList());

        UnescapedText style = rawHtml("<style>\n" + getStyles() + "</style>");

        return SvgTagCreator.svg(style)
                    .attr("version", "1.1")
                    .attr("xmlns", "http://www.w3.org/2000/svg")
                    .attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
                    .attr("width", width.addAndGet(DEFAULT_ICON_SIZE + LABEL_WIDTH / 2))
                    .attr("height", height.addAndGet(DEFAULT_ICON_SIZE))
                    .attr("viewBox", "0 -" + LABEL_WIDTH + " " + (width.get() + LABEL_WIDTH) + " " + (height.get() + LABEL_WIDTH))

                    .with(logo, title)
                    .with(groups)
                    .with(relations.stream().map(SVGRelation::render))
                    .with(items)
                    .with(SvgTagCreator.defs().with(patterns));
    }

    /**
     * Iterates over all items and invokes pathfinding for their relations.
     *
     * @param layouted
     * @param vertexHexes
     * @param pathFinder
     * @return
     */
    private List<SVGRelation> getRelations(LayoutedComponent layouted, Map<LandscapeItem, Hex> vertexHexes, PathFinder pathFinder) {
        List<SVGRelation> relations = new ArrayList<>();
        layouted.getChildren().forEach(layoutedGroup -> {
            layoutedGroup.getChildren().forEach(layoutedItem -> {
                Item item = (Item) layoutedItem.getComponent();
                LOGGER.debug("Adding {} relations for {}", item.getRelations().size(), item.getFullyQualifiedIdentifier());
                item.getRelations().stream()
                        .filter(rel -> rel.getSource().equals(item)) //do not paint twice / incoming (inverse) relations
                        .map(rel -> {
                            return getSvgRelation(vertexHexes, pathFinder, layoutedItem, item, rel);
                        })
                        .filter(Objects::nonNull)
                        .forEach(svgRelation -> relations.add(svgRelation));
            });
        });

        return relations;
    }

    private SVGRelation getSvgRelation(Map<LandscapeItem, Hex> vertexHexes, PathFinder pathFinder, LayoutedComponent layoutedItem, Item item, RelationItem<Item> rel) {
        Hex start = vertexHexes.get(item);
        Hex target = vertexHexes.get(rel.getTarget());
        HexPath bestPath = pathFinder.getPath(start, target);
        if (bestPath != null) {
            SVGRelation svgRelation = new SVGRelation(bestPath, layoutedItem.getColor(), rel);
            LOGGER.debug("Added path for item {} relation {} -> {}", item, rel.getSource(), rel.getTarget());
            return svgRelation;
        }
        LOGGER.error("No path found for item {} relation {}", item, rel);
        return null;
    }

    private String getStyles() {
        String css = "";
        try {
            InputStream resourceAsStream = getClass().getResourceAsStream("/static/css/svg.css");
            css = new String(StreamUtils.copyToByteArray(resourceAsStream));
        } catch (IOException e) {
            LOGGER.error("Failed to load stylesheet /static/css/svg.css");
        }

        return css + "\n" + mapStyleSheetFactory.getMapStylesheet(landscape.getConfig(), landscape.getLog());
    }

    public String getXML() {
        return render().render();
    }
}

