package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupBlacklistTest {

    private LandscapeDescription input;
    private GroupBlacklist blacklist;
    private ProcessLog log;

    @BeforeEach
    public void setup() {
        var landscape = LandscapeFactory.createForTesting("test", "testLandscape").build();
        input = new LandscapeDescription("test", "testLandscape", null);

        input.getWriteAccess().addOrReplaceChild(new GroupDescription("test1"));
        input.getWriteAccess().addOrReplaceChild(new GroupDescription("test2"));

        log = new ProcessLog(LoggerFactory.getLogger(GroupBlacklistTest.class), landscape.getIdentifier());
        input.setProcessLog(log);
        blacklist = new GroupBlacklist();
    }

    @Test
    void resolve() {

        //when
        var out = blacklist.resolve(input);

        //then
        assertEquals(2, out.getReadAccess().all(GroupDescription.class).size());
    }

    @Test
    void removesGroups() {

        //given
        input.getConfig().getGroupBlacklist().add("test1");

        //when
        var out = blacklist.resolve(input);

        //then
        assertThat(out.getReadAccess().all(GroupDescription.class)).hasSize(1);
    }

    @Test
    void newItemIsDeleted() {

        //given
        ItemDescription itemDescription = new ItemDescription("a");
        itemDescription.setGroup("test1");
        input.mergeItems(List.of(itemDescription));

        input.getConfig().getGroupBlacklist().add("test1");

        //when
        var out = blacklist.resolve(input);

        //then
        assertEquals(0, out.getReadAccess().all(ItemDescription.class).size());
        assertEquals(1, out.getReadAccess().all(GroupDescription.class).size());
    }

    @Test
    void testBlacklistOnGroups() {

        //given
        input.getConfig().getGroupBlacklist().add("test2");

        //when
        var out = blacklist.resolve(input);

        //when
        assertEquals(1, out.getReadAccess().all(GroupDescription.class).size());
    }

    @Test
    void testBlacklistOnGroupsWithRegex() {

        //given
        input.getConfig().getGroupBlacklist().add("^test[0-9].*");

        //when
        var out = blacklist.resolve(input);

        //then
        assertThat(out.getReadAccess().all(GroupDescription.class)).hasSize(0);
    }

    @Test
    void removesOnlyMatches() {

        //given
        input.getWriteAccess().addOrReplaceChild(new GroupDescription("foo"));
        input.getConfig().getGroupBlacklist().add("^test[0-9].*");

        //when
        var out = blacklist.resolve(input);

        //then
        assertThat(out.getReadAccess().all(GroupDescription.class)).hasSize(1);
    }

    @Test
    void testBlacklistOnItems() {
        input.getConfig().getGroupBlacklist().add("test2");

        ItemDescription test1item = new ItemDescription();
        test1item.setIdentifier("intest1");
        test1item.setGroup("test1");
        input.getWriteAccess().addOrReplaceChild(test1item);

        ItemDescription test2item = new ItemDescription();
        test2item.setIdentifier("intest2");
        test2item.setGroup("test2");
        input.getWriteAccess().addOrReplaceChild(test2item);


        //when
        var out = blacklist.resolve(input);

        assertEquals(1, out.getReadAccess().all(GroupDescription.class).size());

        //deletes item of blacklisted group
        assertThat(out.getReadAccess().all(ItemDescription.class)).hasSize(1);
    }
}