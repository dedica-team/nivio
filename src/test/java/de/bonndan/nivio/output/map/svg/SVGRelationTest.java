package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.model.Process;
import de.bonndan.nivio.model.ProcessBuilder;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.HexPath;
import de.bonndan.nivio.output.map.hex.MapTile;
import de.bonndan.nivio.output.map.hex.PathTile;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.bonndan.nivio.output.map.svg.SVGDocument.DATA_IDENTIFIER;
import static de.bonndan.nivio.output.map.svg.SVGDocument.VISUAL_FOCUS_UNSELECTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SVGRelationTest {

    private Item foo;
    private Item bar;
    private HexPath hexpath;
    private StatusValue statusValue;
    private GraphTestSupport graph;


    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
        graph.getTestGroup(Layer.domain.name());

        foo = graph.getTestItem(Layer.domain.name(), "foo");
        bar = graph.getTestItem(Layer.domain.name(), "bar");
        hexpath = new HexPath(List.of(
                new PathTile(new MapTile(new Hex(1, 2))),
                new PathTile(new MapTile(new Hex(1, 3))))
        );
        statusValue = new StatusValue(foo.getFullyQualifiedIdentifier(), "bar", Status.GREEN, "");
    }

    @Test
    void relationContainsBothEnds() {

        Relation relation = RelationFactory.create(foo, bar);
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation);

        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", relation, statusValue, null);
        DomContent render = svgRelation.render();
        String render1 = render.render();
        assertThat(render1)
                .contains(foo.getFullyQualifiedIdentifier().getPath())
                .contains(bar.getFullyQualifiedIdentifier().getPath())
                .contains(relation.getFullyQualifiedIdentifier().toString());
    }

    @Test
    void providerRelationsContainsEndpoint() {

        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.PROVIDER);
        graph.landscape.getWriteAccess().addOrReplaceRelation(itemRelationItem);

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue, null);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertTrue(render1.contains("circle"));
    }

    @Test
    void relationIsNotDashedWhenNotPlanned() {

        //only works with provider relations, because dataflow inner path is dashed
        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.PROVIDER);
        graph.landscape.getWriteAccess().addOrReplaceRelation(itemRelationItem);

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue, null);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertFalse(render1.contains("stroke-dasharray"));
    }

    @Test
    void dataflowRelationIsDashed() {

        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.DATAFLOW);
        graph.landscape.getWriteAccess().addOrReplaceRelation(itemRelationItem);

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue, null);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertTrue(render1.contains("stroke-dasharray"));
    }

    @Test
    void supportsVisualFocus() {

        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.DATAFLOW);
        graph.landscape.getWriteAccess().addOrReplaceRelation(itemRelationItem);
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue, null);

        //when
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertThat(render1)
                .contains(DATA_IDENTIFIER)
                .contains(VISUAL_FOCUS_UNSELECTED);
    }

    @Test
    void weightDeterminesStrokeWidth() {

        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.PROVIDER);
        itemRelationItem.setLabel(Label.weight, "2.44");
        graph.landscape.getWriteAccess().addOrReplaceRelation(itemRelationItem);

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue, null);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        var width = Math.round(5 * 2.44f);
        assertThat(render1).contains("stroke-width=\"" + width + "\"");
    }

    @Test
    @DisplayName("The dataflow marker is not null")
    void marker() {
        ContainerTag containerTag = SVGRelation.dataflowMarker();
        assertThat(containerTag).isNotNull();
        assertThat(containerTag.getTagName()).isEqualTo("marker");
    }

    @Test
    @DisplayName("Is translated based on port count")
    void hasTranslation() {
        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.PROVIDER);
        graph.landscape.getWriteAccess().addOrReplaceRelation(itemRelationItem);
        List<PathTile> tiles = hexpath.getTiles();
        hexpath.setPortCount(20);
        PathTile penultimate = tiles.get(tiles.size() - 2);
        for (int i = 0; i < 30; i++) {
            penultimate.getMapTile().incrementPortCount();
        }

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue, null);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertThat(render1).contains("translate(0 -13)");
    }

    @Test
    @DisplayName("renders process")
    void rendersProcess() {
        Relation relation = new Relation(foo, bar, "test", "test", RelationType.PROVIDER);
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation);

        Process foo = ProcessBuilder.aProcess()
                .withParent(graph.landscape)
                .withIdentifier("foo")
                .withBranches(List.of(new Branch(List.of(relation.getFullyQualifiedIdentifier()))))
                .build();
        foo.getLabels().put(Label.color.name(), "orange");


        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", relation, statusValue, foo);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertThat(render1)
                .contains("orange")
                .contains("data-process=\"" + foo.getIdentifier());
    }
}