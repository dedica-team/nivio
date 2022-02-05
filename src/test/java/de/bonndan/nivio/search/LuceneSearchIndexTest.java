package de.bonndan.nivio.search;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

class LuceneSearchIndexTest {

    private LuceneSearchIndex searchIndex;
    private HashSet<GraphComponent> components;
    private Set<SearchDocumentValueObject> valueObjects;
    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        searchIndex = LuceneSearchIndex.createVolatile();

        components = new HashSet<>();
        var index = new Index<GraphComponent>(searchIndex);
        graph = new GraphTestSupport(index);

        Group foo = graph.getTestGroup("foo");

        Item fooa = graph.getTestItemBuilder("foo", "a")
                .withParent(foo)
                .withName("Arnold")
                .withDescription("is a strong guy")
                .withLabels(Map.of(Tagged.LABEL_PREFIX_TAG + "strong", "strong"))
                .build();
        components.add(fooa);
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(fooa);

        Item foos = graph.getTestItemBuilder("foo", "s")
                .withParent(foo)
                .withName("Sylvester")
                .withDescription("is a tough guy")
                .withLabels(Map.of(Tagged.LABEL_PREFIX_TAG + "strong", "strong"))
                .build();
        components.add(foos);
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(foos);

        valueObjects = components.stream()
                .map((GraphComponent component) -> SearchDocumentValueObjectFactory.createForItem((Item) component))
                .collect(Collectors.toSet());

        searchIndex.indexForSearch(valueObjects, new Assessment(Map.of()));
    }

    @Test
    void checksNull() {

        //when
        assertThatThrownBy(() -> searchIndex.indexForSearch(null, new Assessment(Map.of()))).isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> searchIndex.indexForSearch(valueObjects, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void splitOnWhiteSpace() {

        //when
        Set<URI> arnold = searchIndex.search("Arnold");
        assertThat(arnold).hasSize(1);

        //when
        Set<URI> sylvester = searchIndex.search("Sylvester");
        assertThat(sylvester).hasSize(1);

        //when
        Set<URI> both = searchIndex.search("Arnold Sylvester");
        assertThat(both).hasSize(0);

        //when
        Set<URI> orQuery = searchIndex.search("Arnold OR Sylvester");
        assertThat(orQuery).hasSize(2);

        //when
        Set<URI> andQuery = searchIndex.search("Arnold AND Sylvester");
        assertThat(andQuery).hasSize(0);

        //when
        Set<URI> facetDelimiter = searchIndex.search("Sylvester:");
        assertThat(facetDelimiter).isEmpty();
    }

    @Test
    void tags() {

        //when
        Set<URI> strong = searchIndex.search("tag:strong");
        assertThat(strong).hasSize(2);
    }

    @Test
    void caseInsensitive() {

        //when
        Set<URI> strong = searchIndex.search("aRnolD");
        assertThat(strong).hasSize(1);
    }

    @Test
    void partial() {

        //when
        Set<URI> strong = searchIndex.search("aRn");
        assertThat(strong).hasSize(1);
    }


    @Test
    void testFacets() {


        var facets = searchIndex.facets();
        assertThat(facets.getClass()).isEqualTo(ArrayList.class);
        assertThat(facets).hasSize(3);
        List<String> actual = facets.stream().map(facetResult -> facetResult.dim).collect(Collectors.toList());
        assertThat(actual).contains("tag")
                .contains("group")
                .contains("layer");
    }
}
