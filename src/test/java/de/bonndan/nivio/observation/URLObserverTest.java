package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.ReadingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class URLObserverTest {

    private URLObserver urlObserver;
    private FileFetcher fileFetcher;
    private ApplicationEventPublisher publisher;
    private URL url;

    @BeforeEach
    public void setup() throws MalformedURLException {
        fileFetcher = mock(FileFetcher.class);
        publisher = mock(ApplicationEventPublisher.class);
        url = new URL("https://dedica.team");

    }

    @Test
    public void hasChange() throws ExecutionException, InterruptedException {
        when(fileFetcher.get(any(URL.class))).thenReturn("foo", "bar");
        urlObserver = new URLObserver(fileFetcher, url);

        //call again (once in constructor)
        CompletableFuture<String> urlCompletableFuture  = urlObserver.hasChange();
        String s = urlCompletableFuture.get();

        //then
        assertNotNull(s);
        assertEquals(this.url.toString(), s);
    }

    @Test
    public void hasSubsequentChange() throws ExecutionException, InterruptedException {
        when(fileFetcher.get(any(URL.class))).thenReturn("foo", "bar", "baz");
        urlObserver = new URLObserver(fileFetcher, url);

        //call again (once in constructor)
        urlObserver.hasChange();
        //call again
        CompletableFuture<String> urlCompletableFuture  = urlObserver.hasChange();
        String s = urlCompletableFuture.get();

        //then
        assertNotNull(s);
        assertEquals(this.url.toString(), s);
    }

    @Test
    public void hasNoChangeOnInit() throws ExecutionException, InterruptedException {
        when(fileFetcher.get(any(URL.class))).thenReturn("foo");
        urlObserver = new URLObserver(fileFetcher, url);

        CompletableFuture<String> urlCompletableFuture = urlObserver.hasChange();
        assertNull(urlCompletableFuture.get());
    }

    @Test
    public void hasNoChange() throws ExecutionException, InterruptedException {
        when(fileFetcher.get(any(URL.class))).thenReturn("foo");
        urlObserver = new URLObserver(fileFetcher, url);
        urlObserver.hasChange();

        //when
        CompletableFuture<String> urlCompletableFuture = urlObserver.hasChange();
        assertNull(urlCompletableFuture.get());
    }

    @Test
    public void hasError() throws ExecutionException, InterruptedException {
        //given
        when(fileFetcher.get(any(URL.class))).thenThrow(new ReadingException("foo", new RuntimeException("bar")));

        //when
        assertThrows(ProcessingException.class, () -> new URLObserver(fileFetcher, url));
    }
}