package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Assessment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        List<Item> foo1 = landscape.findBy("foo");

        //then
        assertThat(foo1).hasSize(1);
    }

    @Test
    void findByFQI() {

        //when
        List<Item> foo1 = landscape.findBy(foo.getFullyQualifiedIdentifier().toString());

        //then
        assertThat(foo1).hasSize(1);
    }

    @Test
    void isAbsent() {

        //when
        List<Item> foo1 = landscape.findBy("oops");

        //then
        assertThat(foo1).hasSize(0);
    }

    @Test
    @DisplayName("findOne group param is not taken into concern when match is clear")
    void findOneWithoutGroup() {

        //when
        Item foo1 = landscape.findOneBy("foo", null);

        //then
        assertThat(foo1).isNotNull();
    }

    @Test
    @DisplayName("findOne group param is not taken into concern when match is clear")
    void findOneWithGroup() {

        //when
        Item foo1 = landscape.findOneBy("foo", "doesnotmatter");

        //then
        assertThat(foo1).isNotNull();
    }

    @Test
    @DisplayName("findOne picks item from group a when ambiguous")
    void findOneAmbiguousA() {

        foo = ItemFactory.getTestItem("a", "foo");
        bar = ItemFactory.getTestItem("b", "foo");
        landscape = LandscapeFactory.createForTesting("test", "test").withItems(Set.of(foo, bar)).build();

        //when
        Item foo1 = landscape.findOneBy("foo", "a");

        //then
        assertThat(foo1).isNotNull();
        assertThat(foo1.getGroup()).isEqualTo("a");
    }

    @Test
    @DisplayName("findOne picks item from group a when ambiguous")
    void findOneAmbiguousB() {

        foo = ItemFactory.getTestItem("a", "foo");
        bar = ItemFactory.getTestItem("b", "foo");
        landscape = LandscapeFactory.createForTesting("test", "test").withItems(Set.of(foo, bar)).build();

        //when
        Item foo1 = landscape.findOneBy("foo", "b");

        //then
        assertThat(foo1).isNotNull();
        assertThat(foo1.getGroup()).isEqualTo("b");
    }

    @Test
    @DisplayName("findOne throw exception when ambiguous")
    void findOneThrowsIfEmpty() {

        foo = ItemFactory.getTestItem("a", "foo");
        bar = ItemFactory.getTestItem("b", "foo");
        landscape = LandscapeFactory.createForTesting("test", "test").withItems(Set.of(foo, bar)).build();

        //when
        assertThrows(NoSuchElementException.class, () -> landscape.findOneBy("bar", "b"));

    }

    @Test
    @DisplayName("findOne picks item from group a when ambiguous")
    void findOneThrowsIfUnclear() {

        foo = ItemFactory.getTestItem("a", "foo");
        bar = ItemFactory.getTestItem("b", "foo");
        landscape = LandscapeFactory.createForTesting("test", "test").withItems(Set.of(foo, bar)).build();

        //when
        assertThrows(NoSuchElementException.class, () -> landscape.findOneBy("foo", "c"));

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