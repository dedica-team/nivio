package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.linked.ExternalLinkHandler;
import de.bonndan.nivio.input.linked.LinkHandlerFactory;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.bonndan.nivio.input.linked.LinkHandlerFactory.GITHUB;
import static java.util.Optional.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LinksResolverTest {

    private LinkHandlerFactory linkHandlerFactory;
    private LinksResolver resolver;
    private Landscape landscape;
    private LandscapeDescription landscapeDescription;

    @BeforeEach
    void setUp() {
        linkHandlerFactory = mock(LinkHandlerFactory.class);
        resolver = new LinksResolver(mock(ProcessLog.class), linkHandlerFactory);

        landscapeDescription = new LandscapeDescription();
        landscape = new Landscape("foo", new Group(Group.COMMON));
    }

    @Test
    void testProcessLandscapeLinks() throws MalformedURLException {
        //given
        Link link = new Link(new URL("https://github.com/dedica-team/nivio/"), GITHUB);
        landscape.setLinks(Map.of(GITHUB, link));
        ExternalLinkHandler mockResolver = mock(ExternalLinkHandler.class);
        when(linkHandlerFactory.getResolver(eq(GITHUB))).thenReturn(ofNullable(mockResolver));
        when(mockResolver.resolveAndApplyData(eq(link), eq(landscape))).thenReturn(CompletableFuture.completedFuture("OK"));

        //when
        resolver.process(landscapeDescription, landscape);

        //then
        verify(linkHandlerFactory, times(1)).getResolver(eq(GITHUB));
        verify(mockResolver, times(1)).resolveAndApplyData(eq(link), eq(landscape));
    }

    @Test
    void testProcessGroupLinks() throws MalformedURLException {
        //given
        Link link = new Link(new URL("https://github.com/dedica-team/nivio/"), GITHUB);
        Group common = landscape.getGroup(Group.COMMON).orElseThrow();
        common.setLinks(Map.of(GITHUB, link));

        ExternalLinkHandler mockResolver = mock(ExternalLinkHandler.class);
        when(linkHandlerFactory.getResolver(eq(GITHUB))).thenReturn(ofNullable(mockResolver));
        when(mockResolver.resolveAndApplyData(eq(link), eq(common))).thenReturn(CompletableFuture.completedFuture("OK"));

        //when
        resolver.process(landscapeDescription, landscape);

        //then
        verify(linkHandlerFactory, times(1)).getResolver(eq(GITHUB));
        verify(mockResolver, times(1)).resolveAndApplyData(eq(link), eq(common));
    }

    @Test
    void testProcessItemLinks() throws MalformedURLException {
        //given
        Link link = new Link(new URL("https://github.com/dedica-team/nivio/"), GITHUB);
        Group common = landscape.getGroup(Group.COMMON).orElseThrow();
        Item item = new Item(common.getIdentifier(), "foo");
        common.addItem(item);
        item.setLinks(Map.of(GITHUB, link));

        ExternalLinkHandler mockResolver = mock(ExternalLinkHandler.class);
        when(linkHandlerFactory.getResolver(eq(GITHUB))).thenReturn(ofNullable(mockResolver));
        when(mockResolver.resolveAndApplyData(eq(link), eq(item))).thenReturn(CompletableFuture.completedFuture("OK"));

        //when
        resolver.process(landscapeDescription, landscape);

        //then
        verify(linkHandlerFactory, times(1)).getResolver(eq(GITHUB));
        verify(mockResolver, times(1)).resolveAndApplyData(eq(link), eq(item));
    }

    @Test
    void testThrowable() throws MalformedURLException {
        //given
        Link link = new Link(new URL("https://github.com/dedica-team/nivio/"), GITHUB);
        landscape.setLinks(Map.of(GITHUB, link));

        ExternalLinkHandler mockResolver = mock(ExternalLinkHandler.class);
        when(linkHandlerFactory.getResolver(eq(GITHUB))).thenReturn(ofNullable(mockResolver));
        when(mockResolver.resolveAndApplyData(eq(link), eq(landscape))).thenThrow(new RuntimeException("foobar"));

        //when
        resolver.process(landscapeDescription, landscape);

        //then
        verify(linkHandlerFactory, times(1)).getResolver(eq(GITHUB));
        verify(mockResolver, times(1)).resolveAndApplyData(eq(link), eq(landscape));

    }
}