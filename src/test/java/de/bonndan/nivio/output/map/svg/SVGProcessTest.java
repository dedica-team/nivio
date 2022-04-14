package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Process;
import de.bonndan.nivio.model.ProcessBuilder;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.HexPath;
import de.bonndan.nivio.output.map.hex.MapTile;
import de.bonndan.nivio.output.map.hex.PathTile;
import j2html.tags.DomContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SVGProcessTest {

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
    void renders() {
        Relation relation = new Relation(foo, bar, "test", "test", RelationType.PROVIDER);
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation);

        Process foo = ProcessBuilder.aProcess()
                .withParent(graph.landscape)
                .withIdentifier("foo")
                .withBranches(List.of(new Branch(List.of(relation.getFullyQualifiedIdentifier()))))
                .build();
        foo.getLabels().put(Label.color.name(), "orange");

        SVGRelation svgRelation = new SVGRelation(hexpath, "aabbee", relation, statusValue);

        //when
        SVGProcess process = new SVGProcess(foo, List.of(svgRelation));
        DomContent render = process.render();

        //then
        String render1 = render.render();
        assertThat(render1)
                .contains("orange")
                .contains("data-process=\"" + foo.getIdentifier());
    }
}