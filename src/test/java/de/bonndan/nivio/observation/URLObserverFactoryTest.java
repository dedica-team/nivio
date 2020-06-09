package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.FileFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class URLObserverFactoryTest {

    private URLObserverFactory urlObserverFactory;
    private FileFetcher fileFetcher;
    private ApplicationEventPublisher publisher;

    @BeforeEach
    public void setup() {
        fileFetcher = mock(FileFetcher.class);
        publisher = mock(ApplicationEventPublisher.class);
        urlObserverFactory = new URLObserverFactory(publisher, fileFetcher);
    }

    @Test
    public void returnsObserver() throws MalformedURLException {
        URLObserver observer = urlObserverFactory.getObserver(new URL("https://dedica.team"));
        assertNotNull(observer);
    }
}