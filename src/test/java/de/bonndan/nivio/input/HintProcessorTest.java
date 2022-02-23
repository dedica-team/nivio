package de.bonndan.nivio.input;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HintProcessorTest {

    private HintProcessor processor;
    private HintFactory hintFactory;
    private GraphTestSupport graph;

    @BeforeEach
    public void setup() {
        hintFactory = mock(HintFactory.class);
        graph = new GraphTestSupport();
        processor = new HintProcessor(hintFactory);
    }

    @Test
    @DisplayName("label blacklist is used")
    void blacklistPreventsRelations() {
        //given
        graph.landscape.getConfig().getLabelBlacklist().add(".*COMPOSITION.*");

        var bar = graph.getTestItem(graph.groupA.getIdentifier(), "bar");
        bar.setLabel("BAZ_COMPOSITION_URL", "http://baz-composition-service:80");

        var baz = graph.getTestItemBuilder(graph.groupA.getIdentifier(), "baz")
                .withAddress(URI.create("http://baz-composition-service:80"))
                .withParent(graph.groupA)
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(baz);

        //when
        processor.process(graph.landscape);

        //then
        Map<URI, List<Hint>> hints = graph.landscape.getLog().getHints();
        assertThat(hints).isNotNull()
                .containsKey(bar.getFullyQualifiedIdentifier())
                .containsKey(baz.getFullyQualifiedIdentifier());

    }

    @Test
    @DisplayName("label blacklist is used case insensitive")
    void blacklistPreventsRelationsCaseInsensitive() {

        //given
        graph.landscape.getConfig().getLabelBlacklist().add(".*COMPOSITION.*");

        var bar = graph.getTestItem(graph.groupA.getIdentifier(), "bar");
        bar.setLabel("BAZ_composition_URL", "http://baz-composition-service:80");

        var baz = graph.getTestItemBuilder(graph.groupA.getIdentifier(), "baz")
                .withAddress(URI.create("http://baz-composition-service:80"))
                .withParent(graph.groupA)
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(baz);
        graph.landscape.getIndexReadAccess().indexForSearch(Assessment.empty());

        //when
        processor.process(graph.landscape);

        //then
        Map<URI, List<Hint>> hints = graph.landscape.getLog().getHints();
        assertThat(hints).isNotNull()
                .containsKey(bar.getFullyQualifiedIdentifier())
                .containsKey(baz.getFullyQualifiedIdentifier());
    }

    @Test
    void ignoresLinks() {

        //given
        var db = graph.getTestItem(graph.groupA.getIdentifier(), "x.y.z");
        db.setLabel(LabelToFieldResolver.LINK_LABEL_PREFIX + "foo", "http://foo.bar.baz");
        graph.landscape.getIndexReadAccess().indexForSearch(Assessment.empty());

        //when
        processor.process(graph.landscape);

        //then
        Map<URI, List<Hint>> hints = graph.landscape.getLog().getHints();
        assertThat(hints).isNotNull()
                .containsKey(db.getFullyQualifiedIdentifier());
    }
}