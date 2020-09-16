package de.bonndan.nivio.input;

import de.bonndan.nivio.IndexEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IndexEventListenerTest {

    private IndexEventListener listener;
    private Indexer indexer;

    @BeforeEach
    public void setup() {
        indexer = mock(Indexer.class);
        listener =new IndexEventListener(indexer);
    }

    @Test
    public void testOnEvent() {
        LandscapeDescription input = new LandscapeDescription();
        IndexEvent indexEvent = new IndexEvent(this, input, "");
        listener.onApplicationEvent(indexEvent);
        verify(indexer).reIndex(eq(input));
    }
}