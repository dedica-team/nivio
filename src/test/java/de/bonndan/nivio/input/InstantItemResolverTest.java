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
    public void addsNewItems() {

        landscapeDescription = new LandscapeDescription();
        ItemDescription a = new ItemDescription();
        a.setIdentifier("a");
        a.setGroup("a");
        a.getProvidedBy().add("providerA");
        landscapeDescription.getItemDescriptions().add(a);

        ItemDescription b = new ItemDescription();
        b.setIdentifier("b");
        b.setGroup("b");
        a.setRelations(Arrays.asList("adistanttarget"));

        landscapeDescription.getItemDescriptions().add(b);
        landscapeDescription.getConfig().setGreedy(true);

        instantItemResolver.resolve(landscapeDescription);
        assertEquals(4, landscapeDescription.getItemDescriptions().all().size());
    }

    @Test
    public void addsNewItemsOnlyOnce() {

        landscapeDescription = new LandscapeDescription();
        ItemDescription a = new ItemDescription();
        a.setIdentifier("a");
        a.setGroup("a");
        a.getProvidedBy().add("providerA");
        landscapeDescription.getItemDescriptions().add(a);

        ItemDescription b = new ItemDescription();
        b.setIdentifier("b");
        b.setGroup("b");
        a.setRelations(Arrays.asList("adistanttarget"));
        landscapeDescription.getItemDescriptions().add(b);

        ItemDescription c = new ItemDescription();
        c.setIdentifier("c");
        c.setGroup("c");
        c.setRelations(Arrays.asList("adistanttarget"));
        landscapeDescription.getItemDescriptions().add(c);

        landscapeDescription.getConfig().setGreedy(true);


        instantItemResolver.resolve(landscapeDescription);

        //3 given plus 2 resolved (like above)
        assertEquals(5, landscapeDescription.getItemDescriptions().all().size());

        assertTrue(landscapeDescription.getItemDescriptions().all().stream()
                .anyMatch(itemDescription -> itemDescription.getIdentifier().equals("providera"))
        );

        assertTrue(landscapeDescription.getItemDescriptions().all().stream()
                .anyMatch(itemDescription -> itemDescription.getIdentifier().equals("adistanttarget"))
        );
    }
}