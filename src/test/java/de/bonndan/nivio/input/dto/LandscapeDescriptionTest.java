package de.bonndan.nivio.input.dto;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LandscapeDescriptionTest {

    @Test
    void validatesIdentifier() {
        assertThrows(IllegalArgumentException.class, () -> new LandscapeDescription("1/1$"));
    }

    @Test
    void mergeItems() {
        LandscapeDescription landscapeDescription = new LandscapeDescription("identifier", "name", null);

        ItemDescription d = new ItemDescription();
        d.setIdentifier("foo");
        List<ItemDescription> items = new ArrayList<>();
        items.add(d);

        //when
        landscapeDescription.mergeItems(items);

        //then
        assertEquals(1, landscapeDescription.getItemDescriptions().size());
    }

    @Test
    void mergeGroupsWithoutExisting() {
        LandscapeDescription landscapeDescription = new LandscapeDescription("identifier");
        GroupDescription incoming = new GroupDescription();
        incoming.setIdentifier("foo");
        incoming.setColor("00aabb");


        //when
        landscapeDescription.mergeGroups(Set.of(incoming));

        //then
        assertEquals(1, landscapeDescription.getReadAccess().all(GroupDescription.class).size());
        Optional<GroupDescription> foo = landscapeDescription.getReadAccess().matchOneByIdentifiers("foo", null, GroupDescription.class);
        assertThat(foo).isPresent();
        assertThat(foo.get().getColor()).isEqualTo("00aabb");
    }

    @Test
    void mergeGroupsWithExisting() {
        LandscapeDescription landscapeDescription = new LandscapeDescription("identifier");
        GroupDescription existing = new GroupDescription();
        existing.setIdentifier("foo");
        existing.setColor("00aabb");
        existing.setDescription(null);
        landscapeDescription.getWriteAccess().addOrReplaceChild( existing);

        GroupDescription incoming = new GroupDescription();
        incoming.setIdentifier("foo");
        incoming.setColor("ff0033");
        incoming.setDescription("bar");


        //when
        landscapeDescription.mergeGroups(Set.of(incoming));

        //then
        Set<GroupDescription> all = landscapeDescription.getReadAccess().all(GroupDescription.class);
        assertEquals(1, all.size());
        Optional<GroupDescription> foo = landscapeDescription.getReadAccess().matchOneByIdentifiers("foo", null, GroupDescription.class);
        assertThat(foo).isPresent();

        //would not be overwritten
        assertThat(foo.get().getColor()).isEqualTo("00aabb");

        //would be set since not existing
        assertThat(foo.get().getDescription()).isEqualTo("bar");
    }
}