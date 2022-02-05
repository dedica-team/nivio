package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class InstantItemResolverTest {

    private InstantItemResolver instantItemResolver;
    private LandscapeDescription landscapeDescription;

    @BeforeEach
    public void setup() {

        instantItemResolver = new InstantItemResolver(mock(ProcessLog.class));
    }

    @Test
    void addsNewItems() {

        landscapeDescription = new LandscapeDescription("identifier", "name", null);
        ItemDescription a = new ItemDescription();
        a.setIdentifier("a");
        a.setGroup("a");
        a.getProvidedBy().add("providerA");
        landscapeDescription.getWriteAccess().addOrReplaceChild(a);

        ItemDescription b = new ItemDescription();
        b.setIdentifier("b");
        b.setGroup("b");
        a.setRelations(Arrays.asList("adistanttarget"));

        landscapeDescription.getWriteAccess().addOrReplaceChild(b);
        landscapeDescription.getConfig().setGreedy(true);

        //when
        instantItemResolver.resolve(landscapeDescription);

        //then
        assertEquals(4, landscapeDescription.getItemDescriptions().size());
    }

    @Test
    void addsNewItemsOnlyOnce() {

        landscapeDescription = new LandscapeDescription("identifier", "name", null);
        ItemDescription a = new ItemDescription();
        a.setIdentifier("a");
        a.setGroup("a");
        a.getProvidedBy().add("providerA");
        landscapeDescription.getWriteAccess().addOrReplaceChild(a);

        ItemDescription b = new ItemDescription();
        b.setIdentifier("b");
        b.setGroup("b");
        a.setRelations(Arrays.asList("adistanttarget"));
        landscapeDescription.getWriteAccess().addOrReplaceChild(b);

        ItemDescription c = new ItemDescription();
        c.setIdentifier("c");
        c.setGroup("c");
        c.setRelations(Arrays.asList("adistanttarget"));
        landscapeDescription.getWriteAccess().addOrReplaceChild(c);

        landscapeDescription.getConfig().setGreedy(true);


        instantItemResolver.resolve(landscapeDescription);

        //3 given plus 2 resolved (like above)
        assertEquals(5, landscapeDescription.getItemDescriptions().size());

        assertTrue(landscapeDescription.getItemDescriptions().stream()
                .anyMatch(itemDescription -> itemDescription.getIdentifier().equals("providera"))
        );

        assertTrue(landscapeDescription.getItemDescriptions().stream()
                .anyMatch(itemDescription -> itemDescription.getIdentifier().equals("adistanttarget"))
        );
    }
}