package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.input.dto.SourceReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class FileFetcherTest {

    FileFetcher fileFetcher;

    @Mock
    HttpService httpService;

    @BeforeEach
    public void setup() {
        initMocks(this);
        fileFetcher = new FileFetcher(httpService);
    }

    @Test
    public void testRelativeUrlWithBaseUrl() throws IOException, URISyntaxException {
        SourceReference ref = new SourceReference();
        ref.setUrl("./files/one.yml");

        fileFetcher.get(ref, new URL("http://acme.org/somedir"));
        ArgumentCaptor<URL> urlArgumentCaptor = ArgumentCaptor.forClass(URL.class);
        Mockito.verify(httpService, Mockito.times(1)).get(urlArgumentCaptor.capture());

        String s = urlArgumentCaptor.getValue().toString();
        assertEquals("http://acme.org/somedir/files/one.yml", s);
    }

    @Test
    public void testRelativeUrlWithBaseUrl2() throws IOException, URISyntaxException {
        SourceReference ref = new SourceReference();
        ref.setUrl("files/one.yml");

        fileFetcher.get(ref, new URL("http://acme.org/somedir"));
        ArgumentCaptor<URL> urlArgumentCaptor = ArgumentCaptor.forClass(URL.class);
        Mockito.verify(httpService, Mockito.times(1)).get(urlArgumentCaptor.capture());

        String s = urlArgumentCaptor.getValue().toString();
        assertEquals("http://acme.org/somedir/files/one.yml", s);
    }

}
