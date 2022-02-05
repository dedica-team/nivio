package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.external.ExternalLinkHandler;
import de.bonndan.nivio.input.external.LinkHandlerFactory;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Link;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static de.bonndan.nivio.input.external.LinkHandlerFactory.GITHUB;
import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LinksResolverTest {

    private LinkHandlerFactory linkHandlerFactory;
    private LinksResolver resolver;
    private LandscapeDescription landscapeDescription;
    private GroupDescription aGroup;

    @BeforeEach
    void setUp() {
        linkHandlerFactory = mock(LinkHandlerFactory.class);
        resolver = new LinksResolver(mock(ProcessLog.class), linkHandlerFactory);

        landscapeDescription = new LandscapeDescription("foo", "foo", null);
        aGroup = new GroupDescription();
        landscapeDescription.getWriteAccess().addOrReplaceChild( aGroup);
    }

    @Test
    void testProcessLandscapeLinks() throws MalformedURLException {
        //given
        Link link = new Link(new URL("https://github.com/dedica-team/nivio/"), GITHUB);
        landscapeDescription.setLinks(Map.of(GITHUB, link));
        ExternalLinkHandler mockResolver = mock(ExternalLinkHandler.class);
        when(linkHandlerFactory.getResolver(eq(GITHUB))).thenReturn(ofNullable(mockResolver));
        when(mockResolver.resolve(eq(link))).thenReturn(CompletableFuture.completedFuture(new ItemDescription()));

        //when
        resolver.resolve(landscapeDescription);

        //then
        verify(linkHandlerFactory, times(1)).getResolver(eq(GITHUB));
        verify(mockResolver, times(1)).resolve(eq(link));
    }

    @Test
    void testProcessGroupLinks() throws MalformedURLException {
        //given
        Link link = new Link(new URL("https://github.com/dedica-team/nivio/"), GITHUB);
        aGroup.setLinks(Map.of(GITHUB, link));

        ExternalLinkHandler mockResolver = mock(ExternalLinkHandler.class);
        when(linkHandlerFactory.getResolver(eq(GITHUB))).thenReturn(ofNullable(mockResolver));
        when(mockResolver.resolve(eq(link))).thenReturn(CompletableFuture.completedFuture(new GroupDescription()));

        //when
        resolver.resolve(landscapeDescription);

        //then
        verify(linkHandlerFactory, times(1)).getResolver(eq(GITHUB));
        verify(mockResolver, times(1)).resolve(eq(link));
    }

    @Test
    void testProcessItemLinks() throws MalformedURLException {
        //given
        Link link = new Link(new URL("https://github.com/dedica-team/nivio/"), GITHUB);
        ItemDescription item = new ItemDescription("foo");
        item.setLinks(Map.of(GITHUB, link));
        landscapeDescription.getWriteAccess().addOrReplaceChild(item);

        ExternalLinkHandler mockResolver = mock(ExternalLinkHandler.class);
        when(linkHandlerFactory.getResolver(eq(GITHUB))).thenReturn(ofNullable(mockResolver));
        when(mockResolver.resolve(eq(link))).thenReturn(CompletableFuture.completedFuture(new ItemDescription()));

        //when
        resolver.resolve(landscapeDescription);

        //then
        verify(linkHandlerFactory, times(1)).getResolver(eq(GITHUB));
        verify(mockResolver, times(1)).resolve(eq(link));
    }

    @Test
    void testThrowable() throws MalformedURLException {
        //given
        Link link = new Link(new URL("https://github.com/dedica-team/nivio/"), GITHUB);
        landscapeDescription.setLinks(Map.of(GITHUB, link));

        ExternalLinkHandler mockResolver = mock(ExternalLinkHandler.class);
        when(linkHandlerFactory.getResolver(eq(GITHUB))).thenReturn(ofNullable(mockResolver));
        when(mockResolver.resolve(eq(link))).thenThrow(new RuntimeException("foobar"));

        //when
        resolver.resolve(landscapeDescription);

        //then
        verify(linkHandlerFactory, times(1)).getResolver(eq(GITHUB));
        verify(mockResolver, times(1)).resolve(eq(link));
    }


    @Test
    void assignsIncrementsSafely() throws MalformedURLException {
        //given
        Link link = new Link(new URL("https://github.com/dedica-team/nivio/"), GITHUB);
        ItemDescription item = new ItemDescription("foo");
        item.setIcon("foo");
        item.setName("bar");
        item.setLinks(Map.of("github", link));
        landscapeDescription.getWriteAccess().addOrReplaceChild(item);

        ExternalLinkHandler mockResolver = mock(ExternalLinkHandler.class);
        when(linkHandlerFactory.getResolver(eq(GITHUB))).thenReturn(ofNullable(mockResolver));

        ItemDescription increment = new ItemDescription();
        increment.setName("bar2");
        increment.setIcon("foo2");
        increment.setDescription("desc");
        when(mockResolver.resolve(eq(link))).thenReturn(CompletableFuture.completedFuture(increment));

        //when
        resolver.resolve(landscapeDescription);

        //then
        assertThat(item.getIcon()).isEqualTo("foo"); //unchanged
        assertThat(item.getName()).isEqualTo("bar"); //unchanged
        assertThat(item.getDescription()).isEqualTo("desc"); //changed
    }
}