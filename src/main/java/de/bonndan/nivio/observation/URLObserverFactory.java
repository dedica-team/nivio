package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.FileFetcher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class URLObserverFactory {

    private final ApplicationEventPublisher publisher;
    private final FileFetcher fileFetcher;

    public URLObserverFactory(ApplicationEventPublisher publisher, FileFetcher fileFetcher) {
        this.publisher = publisher;
        this.fileFetcher = fileFetcher;
    }

    public URLObserver getObserver(URL url) {
        return new URLObserver(fileFetcher, publisher, url);
    }
}
