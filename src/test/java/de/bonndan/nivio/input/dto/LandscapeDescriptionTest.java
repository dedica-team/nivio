package de.bonndan.nivio.input.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LandscapeDescriptionTest {

    @Test
    void mergeItemsAddsEnv() {
        LandscapeDescription landscapeDescription = new LandscapeDescription("identifier", "name", null);
        ItemDescription d = new ItemDescription();
        d.setIdentifier("foo");
        List<ItemDescription> items = new ArrayList<>();
        items.add(d);

        //when
        landscapeDescription.mergeItems(items);

        //then
        assertEquals(1, landscapeDescription.getItemDescriptions().all().size());
        assertEquals(landscapeDescription.getIdentifier(), d.getEnvironment());
    }

    @Test
    void mergeGroupsWithoutExisting() {
        LandscapeDescription landscapeDescription = new LandscapeDescription("identifier");
        GroupDescription incoming = new GroupDescription();
        incoming.setIdentifier("foo");
        incoming.setColor("00aabb");


        //when
        landscapeDescription.mergeGroups(Map.of("foo", incoming));

        //then
        assertEquals(1, landscapeDescription.getGroups().size());
        assertThat(landscapeDescription.getGroups().get("foo")).isNotNull();
        assertThat(landscapeDescription.getGroups().get("foo").getColor()).isEqualTo("00aabb");
    }

    @Test
    void mergeGroupsWithExisting() {
        LandscapeDescription landscapeDescription = new LandscapeDescription("identifier");
        GroupDescription existing = new GroupDescription();
        existing.setIdentifier("foo");
        existing.setColor("00aabb");
        existing.setDescription(null);
        landscapeDescription.getGroups().put("foo", existing);

        GroupDescription incoming = new GroupDescription();
        incoming.setIdentifier("foo");
        incoming.setColor("ff0033");
        incoming.setDescription("bar");


        //when
        landscapeDescription.mergeGroups(Map.of("foo", incoming));

        //then
        assertEquals(1, landscapeDescription.getGroups().size());
        assertThat(landscapeDescription.getGroups().get("foo")).isNotNull();

        //would not be overwritten
        assertThat(landscapeDescription.getGroups().get("foo").getColor()).isEqualTo("00aabb");

        //would be set since not existing
        assertThat(landscapeDescription.getGroups().get("foo").getDescription()).isEqualTo("bar");
    }
}