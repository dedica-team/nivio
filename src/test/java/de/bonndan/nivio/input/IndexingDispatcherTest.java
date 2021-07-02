package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeSource;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IndexingDispatcherTest {

    private LandscapeDescriptionFactory landscapeDescriptionFactory;
    private ApplicationEventPublisher publisher;
    private ArgumentCaptor<IndexEvent> eventCaptor;
    private IndexingDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        landscapeDescriptionFactory = mock(LandscapeDescriptionFactory.class);
        publisher = mock(ApplicationEventPublisher.class);
        eventCaptor = ArgumentCaptor.forClass(IndexEvent.class);

        dispatcher = new IndexingDispatcher(landscapeDescriptionFactory, publisher);
    }

    @Test
    void createFromBody() {

        LandscapeDescription dto = new LandscapeDescription("identifier");
        when(landscapeDescriptionFactory.fromString(eq("foo"), anyString())).thenReturn(dto);
        //when
        dispatcher.createFromBody("foo");

        //then
        verify(landscapeDescriptionFactory).fromString(eq("foo"), anyString());
        verify(publisher).publishEvent(eventCaptor.capture());
        IndexEvent value = eventCaptor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getLandscapeDescription()).isEqualTo(dto);
    }

    @Test
    void createFromBodyItems() {
        LandscapeDescription dto = new LandscapeDescription("identifier");
        when(landscapeDescriptionFactory.fromBodyItems("foo", "nivio", "body")).thenReturn(dto);
        //when
        dispatcher.createFromBodyItems("foo", "nivio", "body");

        //then
        verify(landscapeDescriptionFactory).fromBodyItems("foo", "nivio", "body");
        verify(publisher).publishEvent(eventCaptor.capture());
        IndexEvent value = eventCaptor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getLandscapeDescription()).isEqualTo(dto);
    }

    @Test
    void fromIncoming() {
        String stringSource = "foo";
        Landscape existing = new Landscape(
                "foobar", Map.of("agroup", new Group("agroup", "foobar")),
                "foobar", null, null, null, new LandscapeSource(stringSource), null, null, Collections.emptyMap()
        );
        LandscapeDescription dto = new LandscapeDescription("foobar");
        when(landscapeDescriptionFactory.fromString(eq(stringSource), anyString())).thenReturn(dto);

        //when
        dispatcher.fromIncoming(existing);

        //then
        verify(landscapeDescriptionFactory).fromString(eq(stringSource), anyString());
        verify(publisher).publishEvent(eventCaptor.capture());
        IndexEvent value = eventCaptor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getLandscapeDescription()).isEqualTo(dto);
    }
}