package de.bonndan.nivio.input;

import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class FileFetcherTest {

    FileFetcher fileFetcher;
    HttpService httpService;

    @BeforeEach
    public void setup() {
        httpService = mock(HttpService.class);
        fileFetcher = new FileFetcher(httpService);
    }

    @Test
    void readFile() {
        File file = new File(RootPath.get() + "/src/test/resources/example/services/wordpress.yml");

        //when
        String s = FileFetcher.readFile(file);

        //then
        assertThat(s).isNotEmpty();
    }

    @Test
    void readFileThrows() {
        assertThrows(ReadingException.class, () -> FileFetcher.readFile(new File("foobar")));
    }

    @Test
    void testRelativeUrlWithBaseUrl() throws IOException, URISyntaxException {

        String s1 = fileFetcher.get("./files/one.yml", new URL("http://acme.org/somedir"));

        ArgumentCaptor<URL> urlArgumentCaptor = ArgumentCaptor.forClass(URL.class);
        Mockito.verify(httpService, Mockito.times(1)).get(urlArgumentCaptor.capture());

        String s = urlArgumentCaptor.getValue().toString();
        assertEquals("http://acme.org/somedir/files/one.yml", s);
    }

}
