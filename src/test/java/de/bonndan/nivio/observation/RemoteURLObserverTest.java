package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.ReadingException;
import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RemoteURLObserverTest {

    private RemoteURLObserver remoteUrlObserver;
    private FileFetcher fileFetcher;
    private ApplicationEventPublisher publisher;
    private URL url;
    private Landscape landscape;

    @BeforeEach
    public void setup() throws MalformedURLException {
        fileFetcher = mock(FileFetcher.class);
        landscape = mock(Landscape.class);
        publisher = mock(ApplicationEventPublisher.class);
        url = new URL("https://dedica.team");

    }

    @Test
    public void detectsChange() {
        when(fileFetcher.get(any(URL.class))).thenReturn("foo", "bar");
        remoteUrlObserver = new RemoteURLObserver(publisher, fileFetcher, url);

        //call again (once in constructor)
        remoteUrlObserver.run();

        //then
        verify(publisher).publishEvent(any(InputChangedEvent.class));
    }

    @Test
    public void hasSubsequentChange() {
        when(fileFetcher.get(any(URL.class))).thenReturn("foo", "bar", "baz");
        remoteUrlObserver = new RemoteURLObserver(publisher, fileFetcher, url);

        //call again (once in constructor)
        remoteUrlObserver.run();

        //call again
        remoteUrlObserver.run();

        //then
        verify(publisher, times(2)).publishEvent(any(InputChangedEvent.class));
    }

    @Test
    public void hasNoChangeOnInit() {
        when(fileFetcher.get(any(URL.class))).thenReturn("foo");
        remoteUrlObserver = new RemoteURLObserver(publisher, fileFetcher, url);

        //when
        remoteUrlObserver.run();

        //then
        verify(publisher, never()).publishEvent(any(InputChangedEvent.class));
    }

    @Test
    public void hasNoChange() {
        when(fileFetcher.get(any(URL.class))).thenReturn("foo");
        remoteUrlObserver = new RemoteURLObserver(publisher, fileFetcher, url);
        remoteUrlObserver.run();

        //when
        remoteUrlObserver.run();

        //then
        verify(publisher, never()).publishEvent(any(InputChangedEvent.class));
    }

    @Test
    public void hasError() {
        //given
        when(fileFetcher.get(any(URL.class))).thenThrow(new ReadingException("foo", new RuntimeException("bar")));

        //when
        assertThrows(ProcessingException.class, () -> new RemoteURLObserver(publisher, fileFetcher, url));
    }
}