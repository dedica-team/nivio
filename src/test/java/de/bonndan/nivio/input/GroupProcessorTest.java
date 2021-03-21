package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GroupProcessorTest {

    private GroupProcessor groupProcessor;
    private Landscape landscape;

    @BeforeEach
    public void setup() {
        landscape = LandscapeFactory.createForTesting("test", "testLandscape").build();
        ProcessLog log = new ProcessLog(LoggerFactory.getLogger(GroupProcessorTest.class));
        groupProcessor = new GroupProcessor(log);
    }

    @Test
    void process() {

        LandscapeDescription input = getLandscapeDescription();

        groupProcessor.process(input, landscape);

        assertEquals(3, landscape.getGroups().size());
    }

    @Test
    void processAddCommonGroup() {

        LandscapeDescription input = getLandscapeDescription();

        ItemDescription item = new ItemDescription();
        item.setIdentifier("abc");
        input.mergeItems(Arrays.asList(item));

        groupProcessor.process(input, landscape);

        assertEquals(3, landscape.getGroups().size());
        assertTrue(landscape.getGroups().containsKey(Group.COMMON));
    }

    @Test
    public void testBlacklistOnGroups() {
        LandscapeDescription input = getLandscapeDescription();
        input.getConfig().getGroupBlacklist().add("test2");

        groupProcessor.process(input, landscape);
        assertEquals(2, landscape.getGroups().size()); //COMMON is always present
        assertTrue(landscape.getGroup("test1").isPresent());
        assertEquals("test1", landscape.getGroup("test1").get().getIdentifier());
    }

    @Test
    public void testBlacklistOnGroupsWithRegex() {
        LandscapeDescription input = getLandscapeDescription();
        input.getConfig().getGroupBlacklist().add("^test[0-9].*");

        groupProcessor.process(input, landscape);
        assertEquals(1, landscape.getGroups().size()); //COMMON only
    }

    @Test
    public void testBlacklistOnItems() {
        LandscapeDescription input = getLandscapeDescription();
        input.getConfig().getGroupBlacklist().add("test2");

        ItemDescription test1item = new ItemDescription();
        test1item.setIdentifier("intest1");
        test1item.setGroup("test1");
        input.getItemDescriptions().add(test1item);

        ItemDescription test2item = new ItemDescription();
        test2item.setIdentifier("intest2");
        test2item.setGroup("test2");
        input.getItemDescriptions().add(test2item);

        Item item = ItemFactory.getTestItem("test2", "foo");
        landscape.getItems().add(item);
        assertEquals(1, landscape.getItems().all().size());

        //when
        groupProcessor.process(input, landscape);

        assertEquals(2, landscape.getGroups().size()); //incl COMMON

        //deletes item of blacklisted group
        assertEquals(0, landscape.getItems().all().size());
        assertEquals(1, input.getItemDescriptions().all().size());
    }

    private LandscapeDescription getLandscapeDescription() {
        LandscapeDescription input = new LandscapeDescription("test", "testLandscape", null);
        input.getGroups().put("test1", new GroupDescription());
        input.getGroups().put("test2", new GroupDescription());
        return input;
    }
}