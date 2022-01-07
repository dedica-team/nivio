package de.bonndan.nivio.api;

import de.bonndan.nivio.config.NivioConfigProperties;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.output.LocalServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LinkFactoryTest {

    private LinkFactory linkFactory;
    private List<Landscape> landscapes;
    private LocalServer localServer;

    @BeforeEach
    void setup() {
        NivioConfigProperties configProperties = mock(NivioConfigProperties.class);
        localServer = mock(LocalServer.class);
        landscapes = List.of(mock(Landscape.class));
        linkFactory = new LinkFactory(localServer, configProperties);
    }

    @Test
    void getIndex() {
        String linkKey = "link";
        Link link = new Link();
        Map<String, Link> oauth2links = Map.of(linkKey, link);

        Index index = linkFactory.getIndex(landscapes);
        index.getOauth2Links().put(linkKey, link);
        assertThat(index.getOauth2Links()).isEqualTo(oauth2links);
    }

    @Test
    void getAuthLinks() throws MalformedURLException {

        //given
        when(localServer.getUrl("/oauth2/authorization/github")).thenReturn(Optional.of(new URL("http://foo.com")));

        //when
        Map<String, Link> authLinks = linkFactory.getAuthLinks();

        //then
        assertThat(authLinks).isNotNull().hasSize(1);
    }
}
