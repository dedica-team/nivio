package de.bonndan.nivio.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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
}