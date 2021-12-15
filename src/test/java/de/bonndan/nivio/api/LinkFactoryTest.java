package de.bonndan.nivio.api;

import de.bonndan.nivio.config.NivioConfigProperties;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.output.LocalServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.*;

import static de.bonndan.nivio.model.Link.LinkBuilder.linkTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LinkFactoryTest {

    private LocalServer localServer;
    private NivioConfigProperties configProperties;
    private NivioConfigProperties.ApiModel config;
    private Landscape landscape;
    private LinkFactory linkFactory;
    Iterable<Landscape> landscapeList;
    private List<Landscape> landscapes;


    @BeforeEach
    public void setup() {
        configProperties = mock(NivioConfigProperties.class);
        localServer = mock(LocalServer.class);
        config = mock(NivioConfigProperties.ApiModel.class);
        landscape = mock(Landscape.class);
        landscapeList = mock(Iterable.class);
        landscapes = List.of(mock(Landscape.class));
        linkFactory = new LinkFactory(localServer, configProperties);

    }

    @Test
    public void getIndex() {
        Index index = new Index(config);
    }
//
//        String linkKey = "link1";
//        Link link = new Link();
//        Map<String, Link> oauth2links = Map.of(linkKey, link);
//
//        index.getOauth2Links().put(linkKey, link);
////        when(landscapeList.iterator().next()).thenReturn(landscape);
//
////        linkFactory.getIndex(landscapeList).equals(index);
//
//        // when
//        when(linkFactory.getIndex(landscapeList).getOauth2Links()).thenReturn(index.getOauth2Links());
//
//        // then
//
//        assertEquals(linkFactory.getIndex(landscapes).getOauth2Links(),(oauth2links));
//
////        Index index = new Index(config);
////        String linkKey = "link1";
////        Link link = new Link();
////        Map<String, Link> oauth2links = Map.of(linkKey, link);
////        index.getLinks().put(linkKey, link);
////
////        when(linkFactory.getIndex(landscapes)).thenReturn(index);
////        // when
////        index.getLinks().put(linkKey, link);
////
////        assertEquals("test", linkFactory.getIndex(landscapes));
//
//    }
}
