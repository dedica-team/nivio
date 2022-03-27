package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.RendererOptions;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.HexMap;
import de.bonndan.nivio.output.map.hex.MapTile;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.*;

import static j2html.TagCreator.rawHtml;


/**
 * Creates an SVG document based on pre-rendered map items.
 */
public class SVGDocument extends Component {

    private static final Logger LOGGER = LoggerFactory.getLogger(SVGDocument.class);
    public static final int PADDING = 10;
    public static final int LABEL_WIDTH = 140;
    public static final String VISUAL_FOCUS_UNSELECTED = "unselected";
    public static final String DATA_IDENTIFIER = "data-identifier";

    private final LayoutedComponent layouted;
    private final Landscape landscape;
    private final String cssStyles;
    private final Assessment assessment;
    private final boolean debug;
    private final HexMap hexMap;

    public SVGDocument(@NonNull final LayoutedComponent layouted,
                       @NonNull final HexMap hexMap,
                       @NonNull final RendererOptions options,
                       @Nullable final String cssStyles
    ) {
        this.layouted = Objects.requireNonNull(layouted);
        this.hexMap = Objects.requireNonNull(hexMap);
        this.landscape = (Landscape) layouted.getComponent();
        this.assessment = options.getAssessment();
        this.debug = options.isDebug();
        this.cssStyles = !StringUtils.hasLength(cssStyles) ? "" : cssStyles;
    }

    public DomContent render() {

        List<DomContent> defs = new ArrayList<>();
        List<SVGItem> items = new ArrayList<>();

        defs.add(SVGStatus.glowFilter());
        defs.add(SVGStatus.patternFor(Status.UNKNOWN));
        defs.add(SVGStatus.patternFor(Status.GREEN));
        defs.add(SVGStatus.patternFor(Status.YELLOW));
        defs.add(SVGStatus.patternFor(Status.ORANGE));
        defs.add(SVGStatus.patternFor(Status.RED));
        defs.add(SVGStatus.patternFor(Status.BROWN));
        //transform all item positions to hex map positions
        layouted.getChildren().forEach(group -> {
            if (debug) {
                LOGGER.debug("rendering group {} with items {}", group.getComponent().getIdentifier(), group.getChildren());
            }
            group.getChildren().forEach(layoutedItem -> {

                Item item = (Item) layoutedItem.getComponent();

                //collect patterns for icons
                if (StringUtils.hasLength(layoutedItem.getFill())) {
                    SVGPattern svgPattern = new SVGPattern(layoutedItem.getFill());
                    defs.add(svgPattern.render());
                }

                //render icons
                SVGItemLabel label = new SVGItemLabel(item);
                Point2D.Double pos = hexMap.getTileForItem(item).getHex().toPixel();

                List<StatusValue> itemStatuses = assessment.getResults().get(item.getFullyQualifiedIdentifier());
                items.add(new SVGItem(label.render(), layoutedItem, itemStatuses, pos));
            });
        });

        List<SVGGroupArea> groupAreas = new ArrayList<>();
        layouted.getChildren().forEach(groupLayout -> {
            Group group = (Group) groupLayout.getComponent();
            Set<MapTile> groupArea = hexMap.getGroupArea(group);
            SVGGroupArea area = SVGGroupArea.forGroup(group, groupArea, debug);
            groupAreas.add(area);
        });

        defs.add(SVGRelation.dataflowMarker());
        List<SVGRelation> relations = getRelations(layouted);

        /*
         * Transformation from cartesian to hex coords can turn the resulting cartesian coords of the hexes into negative
         * This could be fixed with a viewbox, but since the frontend svg lib is somehow broken (#438), we need to shift
         * all components into the all-positive quadrant.
         */
        SVGDimension dimension = SVGDimensionFactory.getDimension(groupAreas, relations);
        double extraPadding = Hex.HEX_SIZE;
        var offset = new Point2D.Double(
                dimension.cartesian.horMin * -1 + extraPadding,
                dimension.cartesian.vertMin * -1 + extraPadding
        );

        items.forEach(svgItem -> svgItem.shift(offset));
        groupAreas.forEach(svgGroupArea -> svgGroupArea.shift(offset));
        relations.forEach(svgRelation -> svgRelation.shift(offset));

        //render background hexes
        defs.add(SVGBackgroundFactory.getHex());

        List<DomContent> background = new ArrayList<>(
                //SVGBackgroundFactory.getBackgroundTiles(dimension)
        );

        DomContent title = getTitle(dimension, offset);
        DomContent logo = getLogo(dimension, offset);

        UnescapedText style = rawHtml("<style>\n" + cssStyles + "</style>");


        return SvgTagCreator.svg(style)
                .attr("version", "1.1")
                .attr("xmlns", "http://www.w3.org/2000/svg")
                .attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
                .attr(SVGAttr.WIDTH, dimension.cartesian.horMax - dimension.cartesian.horMin + 3 * extraPadding)
                .attr(SVGAttr.HEIGHT, dimension.cartesian.vertMax - dimension.cartesian.vertMin + 3 * extraPadding)
                .attr(SVGAttr.CLASS, "map")

                .with(background)
                .with(logo, title)
                .with(groupAreas.stream().map(SVGGroupArea::render))
                .with(relations.stream().map(SVGRelation::render))
                //draw items above relations
                .with(items.stream().map(SVGItem::render))
                //defs contain reusable stuff
                .with(SvgTagCreator.defs().with(defs));
    }

    @Nullable
    private DomContent getLogo(SVGDimension dimension, Point2D.Double offset) {
        DomContent logo = null;
        String logoUrl = landscape.getLabel(Label._icondata); //has been set by appearance resolver
        if (StringUtils.hasLength(logoUrl)) {
            logo = SvgTagCreator.image()
                    .attr("xlink:href", logoUrl)
                    .attr("x", dimension.cartesian.horMin + PADDING + offset.x)
                    .attr("y", dimension.cartesian.vertMin + PADDING + offset.x + 80)
                    .attr(SVGAttr.WIDTH, LABEL_WIDTH)
                    .attr(SVGAttr.HEIGHT, LABEL_WIDTH)
                    .attr(SVGAttr.CLASS, "logo");
        }
        return logo;
    }

    private ContainerTag getTitle(SVGDimension dimension, Point2D.Double offset) {
        return SvgTagCreator.text(landscape.getName())
                .attr("x", dimension.cartesian.horMin + PADDING + offset.x)
                .attr("y", dimension.cartesian.vertMin + PADDING + 60 + offset.y)
                .attr(SVGAttr.CLASS, "title");
    }

    /**
     * Iterates over all items and invokes pathfinding for their relations.
     */
    private List<SVGRelation> getRelations(LayoutedComponent layouted) {

        List<SVGRelation> relations = new ArrayList<>();
        layouted.getChildren().forEach(
                layoutedGroup -> layoutedGroup.getChildren().forEach(layoutedItem -> {
                    Item item = (Item) layoutedItem.getComponent();
                    LOGGER.debug("Adding {} relations for {}", item.getRelations().size(), item.getFullyQualifiedIdentifier());
                    item.getRelations().stream()
                            .filter(relation -> relation.getSource().equals(item))
                            .map(rel -> hexMap.getPath(rel)
                                    .map(hexPath -> new SVGRelation(
                                                    hexPath,
                                                    layoutedItem.getColor(),
                                                    rel, null,
                                                    rel.getProcess().orElse(null)
                                            )
                                    ).orElse(null)
                            )
                            .filter(Objects::nonNull)
                            .forEach(relations::add);
                }));

        return relations;
    }

    public String getXML() {
        return render().render();
    }
}