package de.bonndan.nivio.output.map;

import j2html.tags.DomContent;
import j2html.tags.UnescapedText;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static j2html.TagCreator.rawHtml;

class SvgFactory extends Component {

    private int size = 40;
    private int padding = 10;
    private Map<String, ItemMapItem> byId = new HashMap<>();
    private List<Hex> occupied = new ArrayList<>();

    private final RenderedXYMap map;

    SvgFactory(RenderedXYMap map) {
        this.map = map;
    }

    public DomContent render() {
        String css = "";
        try {
            InputStream resourceAsStream = getClass().getResourceAsStream("/static/css/svg.css");
            css = new String(StreamUtils.copyToByteArray(resourceAsStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 200 to add some whitespace in svg
        Layout layout = new Layout(size, 1.1, new Point2D.Double(200, 200));

        map.items.forEach(vertex -> {
            var hexCoords = new HexCoords(vertex.x, vertex.y, map.sizeFactor);
            vertex.hex = hexCoords.toHex();
            byId.put(vertex.id, vertex);
            occupied.add(vertex.hex);
        });

        var pathFinder = new PathFinder(occupied);
        UnescapedText style = rawHtml("<style>\n" + css + "</style>");

        return
                SvgTagCreator.svg(style)
                        .attr("version", "1.1")
                        .attr("xmlns", "http://www.w3.org/2000/svg")
                        .attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
                        .attr("width", map.width *4)
                        .attr("height", map.height *5)

                        //groups
                        .with(
                                map.groups.stream().map(group -> {
                                    NGroup nGroup = new NGroup(group, layout, map.sizeFactor);
                                    return nGroup.render();
                                }).collect(Collectors.toList())
                        )

                        //relations
                        .with(
                                map.items.stream().flatMap(vertex -> {

                                            return vertex.relations.stream().map(rel -> {
                                                var paths = new CopyOnWriteArrayList<TilePath>();
                                                var path0 = new TilePath(vertex.hex);
                                                paths.add(path0);
                                                Hex target = byId.get(rel.target).hex;
                                                pathFinder.findPaths(paths, target);
                                                var path = pathFinder.sortAndFilterPaths(paths);
                                                NPath nPath = new NPath(path, layout, vertex.color, rel);
                                                return nPath.render();
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
                                    var width = 200;
                                    NLabel nLabel = new NLabel(vertex, width, size, padding);

                                    Nexagon nexagon = new Nexagon(nLabel.render(), vertex.hex, layout, fill, "stroke: #" + vertex.color);
                                    return nexagon.render();
                                }).collect(Collectors.toList())
                        )

                        .with(
                                map.items.stream()
                                        .filter(vertex -> !StringUtils.isEmpty(vertex.image))
                                        .map(vertex -> {
                                            var fill = vertex.image;
                                            var id = Base64.getEncoder().encodeToString(vertex.id.getBytes());
                                            NPattern nPattern = new NPattern(id, fill, size - padding, padding);
                                            return nPattern.render();
                                        }).collect(Collectors.toList())
                        );


    }

    public String getXML() {
        return "<?xml version=\"1.0\" standalone=\"yes\"?>" + render().render();
    }
}

