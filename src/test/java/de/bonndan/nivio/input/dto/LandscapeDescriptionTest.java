package de.bonndan.nivio.input.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LandscapeDescriptionTest {

    @Test
    void addItemsAddsEnv() {
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
}