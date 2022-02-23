package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class FullyQualifiedIdentifierTest {


    @Test
    void buildWithSimpleId() {

        //when
        URI test = FullyQualifiedIdentifier.build(ComponentClass.landscape, "test");

        //then
        assertThat(test).isNotNull().isEqualTo(URI.create("landscape://test/"));
    }

    @Test
    void buildWithComplexLandscapeId() {

        //when
        URI test = FullyQualifiedIdentifier.build(ComponentClass.landscape, "nivio:demo-1_foo");

        //then
        assertThat(test).isNotNull().isEqualTo(URI.create("landscape://nivio:demo-1_foo/"));
    }

    @Test
    void buildWithItem() {

        //when
        URI test = FullyQualifiedIdentifier.build(ComponentClass.item, "nivio:demo-1_foo", "default", "default","aGroup", "foo");

        //then
        assertThat(test).isNotNull().isEqualTo(URI.create("item://nivio:demo-1_foo/default/default/aGroup/foo"));
    }

    @Test
    void buildWithContext() {

        //when
        URI test = FullyQualifiedIdentifier.build(ComponentClass.context, "nivio:demo-1_foo", "aUnit", "aContext");

        //then
        assertThat(test).isNotNull().isEqualTo(URI.create("context://nivio:demo-1_foo/aUnit/aContext"));
    }

    @Test
    void forDescription() {

        //when
        URI uri = FullyQualifiedIdentifier.forDescription(ItemDescription.class, "landscape", "foo", "bar", null, null, null);

        //then
        assertThat(uri).isNotNull().isEqualTo(URI.create("item://landscape/foo/bar"));
    }

    @Test
    void forDescriptionWithMissingParts() {

        //when
        URI uri = FullyQualifiedIdentifier.forDescription(ItemDescription.class, "", null, "bar", null, null, null);

        //then
        assertThat(uri).isNotNull().isEqualTo(URI.create("item://_/_/bar"));
    }

    @Test
    void fromParentWithItem() {

        Group group = GroupBuilder.aTestGroup("foo").withParent(ContextBuilder.aTestContext("test").build()).build();
        Item item = ItemFactory.getTestItemBuilder(group.getIdentifier(), "bar").withParent(group).build();

        //when
        URI uri = FullyQualifiedIdentifier.from(group.getFullyQualifiedIdentifier(), item);

        //then
        assertThat(uri).isNotNull().isEqualTo(URI.create("item://test/testunit/test/foo/bar"));
    }

    @Test
    void isUndefined() {
        assertThat(FullyQualifiedIdentifier.isUndefined(FullyQualifiedIdentifier.UNDEFINED)).isTrue();
        assertThat(FullyQualifiedIdentifier.isUndefined(null)).isTrue();
        assertThat(FullyQualifiedIdentifier.isUndefined("")).isTrue();
        assertThat(FullyQualifiedIdentifier.isUndefined("a")).isFalse();
    }
}