package de.bonndan.nivio.api;

import de.bonndan.nivio.config.NivioConfigProperties;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.output.LocalServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

class LinkFactoryTest {

    private LinkFactory linkFactory;
    private List<Landscape> landscapes;

    @BeforeEach
    void setup() {
        NivioConfigProperties configProperties = mock(NivioConfigProperties.class);
        LocalServer localServer = mock(LocalServer.class);
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

}
