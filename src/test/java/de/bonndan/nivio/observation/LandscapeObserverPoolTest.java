package de.bonndan.nivio.observation;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LandscapeObserverPoolTest {

    @Test
    public void hasChange() {
        URLObserver urlObserver1 = mock(URLObserver.class);
        URLObserver urlObserver2 = mock(URLObserver.class);
        when(urlObserver1.hasChange()).thenReturn(CompletableFuture.completedFuture("hello"));
        when(urlObserver2.hasChange()).thenReturn(CompletableFuture.completedFuture("world"));
        Landscape landscape = new Landscape("test", new Group(Group.COMMON), "testLandscape");
        LandscapeObserverPool landscapeObserverPool = new LandscapeObserverPool(landscape, List.of(urlObserver1, urlObserver2));

        //when
        ObservedChange change = landscapeObserverPool.getChange();

        //then
        assertEquals(2, change.getChanges().size());
        assertEquals("hello;world", StringUtils.collectionToDelimitedString(change.getChanges(), ";"));
    }

}