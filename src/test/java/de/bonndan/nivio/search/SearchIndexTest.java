package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchIndexTest {

    private SearchIndex searchIndex;

    @BeforeEach
    void setup() {
        searchIndex = new SearchIndex("test");

        Set<Item> components = new HashSet<>();
        components.add(ItemFactory.getTestItemBuilder("foo", "a")
                .withName("Arnold")
                .withDescription("is a strong guy")
                .withLabels(Map.of(Tagged.LABEL_PREFIX_TAG + "strong", "strong"))
                .build());
        components.add(ItemFactory.getTestItemBuilder("foo", "s")
                .withName("Sylvester")
                .withDescription("is a tough guy")
                .withLabels(Map.of(Tagged.LABEL_PREFIX_TAG + "strong", "strong"))
                .build());

        Landscape landscape = LandscapeFactory.createForTesting("test", "test").withItems(components).build();
        searchIndex.indexForSearch(landscape, new Assessment(Map.of()));
    }

    @Test
    void checksNull() {

        //given
        Landscape landscape = LandscapeFactory.createForTesting("test", "test").build();
        Assessment assessment = new Assessment(Map.of());

        //when
        assertThatThrownBy(() -> searchIndex.indexForSearch(null, assessment)).isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> searchIndex.indexForSearch(landscape, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void splitOnWhiteSpace() {

        //when
        Set<FullyQualifiedIdentifier> arnold = searchIndex.search("Arnold");
        assertThat(arnold).hasSize(1);

        //when
        Set<FullyQualifiedIdentifier> sylvester = searchIndex.search("Sylvester");
        assertThat(sylvester).hasSize(1);

        //when
        Set<FullyQualifiedIdentifier> both = searchIndex.search("Arnold Sylvester");
        assertThat(both).hasSize(0);

        //when
        Set<FullyQualifiedIdentifier> orQuery = searchIndex.search("Arnold OR Sylvester");
        assertThat(orQuery).hasSize(2);

        //when
        Set<FullyQualifiedIdentifier> andQuery = searchIndex.search("Arnold AND Sylvester");
        assertThat(andQuery).hasSize(0);

        //when
        Set<FullyQualifiedIdentifier> facetDelimiter = searchIndex.search("Sylvester:");
        assertThat(andQuery).hasSize(0);
    }

    @Test
    void tags() {

        //when
        Set<FullyQualifiedIdentifier> strong = searchIndex.search("tag:strong");
        assertThat(strong).hasSize(2);
    }

    @Test
    void caseInsensitive() {

        //when
        Set<FullyQualifiedIdentifier> strong = searchIndex.search("aRnolD");
        assertThat(strong).hasSize(1);
    }

    @Test
    void partial() {

        //when
        Set<FullyQualifiedIdentifier> strong = searchIndex.search("aRn");
        assertThat(strong).hasSize(1);
    }

    @Test
    void searchIndexIOException() {
        var searchIndex = new SearchIndex("null");
    }


    @Test
    void testFacets() {
        var facets = searchIndex.facets();
        assertThat(facets.getClass()).isEqualTo(ArrayList.class);
        assertThat(facets.size()).isEqualTo(2);
    }
}