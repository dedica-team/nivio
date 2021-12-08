package de.bonndan.nivio.api;

import de.bonndan.nivio.config.NivioConfigProperties;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.output.LocalServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Map;
import java.util.Optional;

import static de.bonndan.nivio.model.Link.LinkBuilder.linkTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LinkFactoryTest {

    private LocalServer localServer;
    private NivioConfigProperties configProperties;
    private NivioConfigProperties.ApiModel config;
    private Index index;
    Iterable<Landscape> landscapes;
    private LinkFactory linkFactory = new LinkFactory(localServer, configProperties);

    @BeforeEach
    public void setup() {
        configProperties = mock(NivioConfigProperties.class);
        localServer = mock(LocalServer.class);
        index = mock(Index.class);
        config = mock(NivioConfigProperties.ApiModel.class);

    }

    @Test
    public void getIndex() {
//        Index index = new Index(config);
//        String linkKey = "link1";
//        Link link = new Link();
//        Map<String, Link> oauth2links = Map.of(linkKey, link);
//        index.getLinks().put(linkKey, link);
//
//        when(linkFactory.getIndex(landscapes)).thenReturn(index);
//        // when
//        index.getLinks().put(linkKey, link);
//
//        assertEquals("test", linkFactory.getIndex(landscapes));

    }
}
