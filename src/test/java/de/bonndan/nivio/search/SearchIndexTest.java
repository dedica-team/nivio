package de.bonndan.nivio.search;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.Tagged;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SearchIndexTest {

    private SearchIndex searchIndex;

    @BeforeEach
    void setup() {
        searchIndex = new SearchIndex();

        Set<Component> components = new HashSet<>();
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
        searchIndex.indexForSearch(components);
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
    }

    @Test
    void tags() {

        //when
        Set<FullyQualifiedIdentifier> strong = searchIndex.search("tag:strong");
        assertThat(strong).hasSize(2);
    }
}