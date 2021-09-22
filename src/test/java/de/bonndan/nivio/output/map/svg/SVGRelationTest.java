package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.MapTile;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
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


    @BeforeEach
    void setup() {
        Landscape landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();
        foo = getTestItem(Layer.domain.name(), "foo", landscape);
        bar = getTestItem(Layer.domain.name(), "bar", landscape);
        hexpath = new HexPath(List.of(new MapTile(new Hex(1, 2)), new MapTile(new Hex(1, 3))));
        statusValue = new StatusValue("foo", Status.GREEN);
    }

    @Test
    @DisplayName("items without groups use proper fqi")
    void relationContainsBothEnds() {

        Relation itemRelationItem = RelationFactory.createForTesting(foo, bar);
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue);
        DomContent render = svgRelation.render();
        String render1 = render.render();
        assertTrue(render1.contains("l1/domain/foo"));
        assertFalse(render1.contains("l1//foo"));
        assertTrue(render1.contains("l1/domain/bar"));
        assertFalse(render1.contains("l1//bar"));
    }

    @Test
    void providerRelationsContainsEndpoint() {

        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.PROVIDER);

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertTrue(render1.contains("circle"));
    }

    //@Disabled // dataflow has no endpoint marker anymore/yet
    @Test
    void dataflowRelationsContainsEndpoint() {

        //given
        Relation relation = new Relation(foo, bar, "test", "test", RelationType.DATAFLOW);

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", relation, statusValue);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertTrue(render1.contains("url(#" + SVGRelation.MARKER_ID + ")"));
    }

    @Test
    void relationIsNotDashedWhenNotPlanned() {

        //only works with provider relations, because dataflow inner path is dashed
        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.PROVIDER);

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertFalse(render1.contains("stroke-dasharray"));
    }

    @Test
    void dataflowRelationIsDashed() {

        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.DATAFLOW);

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertTrue(render1.contains("stroke-dasharray"));
    }

    @Test
    void supportsVisualFocus() {

        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.DATAFLOW);
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue);

        //when
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertThat(render1).contains(DATA_IDENTIFIER);
        assertThat(render1).contains(VISUAL_FOCUS_UNSELECTED);
    }

    @Test
    void weightDeterminesStrokeWidth() {

        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.PROVIDER);
        itemRelationItem.setLabel(Label.weight, "2.44");

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue);
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

}