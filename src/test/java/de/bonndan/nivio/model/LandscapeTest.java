package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Assessment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.junit.jupiter.api.Assertions.*;

class LandscapeTest {

    private Item foo;
    private Item bar;
    private Landscape landscape;

    @BeforeEach
    void setup() {
        foo = ItemFactory.getTestItem("a", "foo");
        bar = ItemFactory.getTestItem("b", "bar");
        landscape = LandscapeFactory.createForTesting("test", "test").withItems(Set.of(foo, bar)).build();
    }

    @Test
    void findByIdentifier() {

        //when
        Optional<Item> foo1 = landscape.findBy("foo");

        //then
        assertThat(foo1).isPresent();
    }

    @Test
    void findByFQI() {

        //when
        Optional<Item> foo1 = landscape.findBy(foo.getFullyQualifiedIdentifier().toString());

        //then
        assertThat(foo1).isPresent();
    }

    @Test
    void isAbsent() {

        //when
        Optional<Item> foo1 = landscape.findBy("oops");

        //then
        assertThat(foo1).isEmpty();
    }

    @Test
    public void searchStartingWithWildcard() throws URISyntaxException {
        //given
        ArrayList<Item> items = new ArrayList<>();

        Item s1 = getTestItemBuilder("g1", "s1").withName("foo").withLandscape(landscape).build();
        items.add(s1);

        Item s2 = getTestItemBuilder("g1", "s2").withName("bar").withLandscape(landscape).build();
        items.add(s2);

        Item s3 = getTestItemBuilder("g2", "hasaddress").withAddress(new URI("https://foo.bar/")).withLandscape(landscape).build();
        items.add(s3);

        landscape.setItems(new HashSet<>(items));

        landscape.getSearchIndex().indexForSearch(landscape, new Assessment(Map.of()));

        //when
        Set<Item> search = landscape.search("*oo");

        //then
        assertEquals(1, search.size());
    }
}