package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.LandscapeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GroupResolverTest {

    private GroupResolver groupResolver;

    @BeforeEach
    public void setup() {
        ProcessLog log = mock(ProcessLog.class);
        groupResolver = new GroupResolver(log);
    }

    @Test
    void process() {

        LandscapeDescription input = new LandscapeDescription();
        input.getGroups().put("test1", new GroupDescription());
        input.getGroups().put("test2", new GroupDescription());
        LandscapeImpl landscape = new LandscapeImpl();

        groupResolver.process(input, landscape);

        assertEquals(2, landscape.getGroups().size());
    }

    @Test
    void processAddCommonGroup() {

        LandscapeDescription input = new LandscapeDescription();
        input.getGroups().put("test1", new GroupDescription());
        input.getGroups().put("test2", new GroupDescription());

        ItemDescription item = new ItemDescription();
        item.setIdentifier("abc");
        input.addItems(Arrays.asList(item));

        LandscapeImpl landscape = new LandscapeImpl();


        groupResolver.process(input, landscape);

        assertEquals(3, landscape.getGroups().size());
        assertTrue(landscape.getGroups().containsKey(Group.COMMON));
    }
}