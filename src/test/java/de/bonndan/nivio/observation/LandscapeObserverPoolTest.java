package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
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
        LandscapeDescription description = new LandscapeDescription();
        description.setIdentifier("test");
        LandscapeObserverPool landscapeObserverPool = new LandscapeObserverPool(description, List.of(urlObserver1, urlObserver2));

        //when
        ObservedChange change = landscapeObserverPool.getChange();

        //then
        assertEquals(2, change.getChanges().size());
        assertEquals("hello;world", StringUtils.collectionToDelimitedString(change.getChanges(), ";"));
    }

}