package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SVGRelationTest {

    private Landscape landscape;
    private Item foo;
    private Item bar;
    private HexPath hexpath;
    private StatusValue statusValue;


    @BeforeEach
    void setup() {
         landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();
         foo = getTestItem(Group.COMMON, "foo", landscape);
         bar = getTestItem(Group.COMMON, "bar", landscape);
         hexpath = new HexPath(List.of(new Hex(1,2), new Hex(1,3)));
         statusValue = new StatusValue("foo", Status.GREEN);
    }

    @Test
    @DisplayName("items without groups use proper fqi")
    public void relationContainsBothEnds() {

        Relation itemRelationItem = new Relation(foo, bar);
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue);
        DomContent render = svgRelation.render();
        String render1 = render.render();
        assertTrue(render1.contains("l1/common/foo"));
        assertFalse(render1.contains("l1//foo"));
        assertTrue(render1.contains("l1/common/bar"));
        assertFalse(render1.contains("l1//bar"));
    }

    @Test
    public void providerRelationsContainsEndpoint() {

        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.PROVIDER);

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertTrue(render1.contains("circle"));
    }

    @Test
    public void dataflowRelationsContainsEndpoint() {

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
    public void relationIsNotDashedWhenNotPlanned() {

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
    public void plannedRelationIsDashed() {
        foo.setLabel(Label.lifecycle, Lifecycle.PLANNED.name());

        Relation itemRelationItem = new Relation(foo, bar, "test", "test", RelationType.PROVIDER);

        //when
        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", itemRelationItem, statusValue);
        DomContent render = svgRelation.render();

        //then
        String render1 = render.render();
        assertTrue(render1.contains("stroke-dasharray"));
    }

    @Test
    @DisplayName("The dataflow marker is not null")
    public void marker() {
        ContainerTag containerTag = SVGRelation.dataflowMarker();
        assertThat(containerTag).isNotNull();
        assertThat(containerTag.getTagName()).isEqualTo("marker");

    }

}