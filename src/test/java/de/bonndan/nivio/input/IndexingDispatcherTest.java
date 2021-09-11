package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.Source;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IndexingDispatcherTest {

    private SeedConfigurationFactory configurationFactory;
    private LandscapeDescriptionFactory landscapeDescriptionFactory;
    private ApplicationEventPublisher publisher;
    private ArgumentCaptor<IndexEvent> eventCaptor;
    private IndexingDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        configurationFactory = mock(SeedConfigurationFactory.class);
        landscapeDescriptionFactory = mock(LandscapeDescriptionFactory.class);
        publisher = mock(ApplicationEventPublisher.class);
        eventCaptor = ArgumentCaptor.forClass(IndexEvent.class);
        SourceReferencesResolver resolver = mock(SourceReferencesResolver.class);

        dispatcher = new IndexingDispatcher(configurationFactory, landscapeDescriptionFactory, publisher, resolver);
    }

    @Test
    void createLandscapeDescriptionFromBody() {

        LandscapeDescription dto = new LandscapeDescription("identifier");
        when(landscapeDescriptionFactory.fromString(eq("foo"), anyString())).thenReturn(dto);

        //when
        dispatcher.createLandscapeDescriptionFromBody("foo");

        //then
        verify(publisher).publishEvent(eventCaptor.capture());
        IndexEvent value = eventCaptor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getLandscapeDescriptions()).contains(dto);
    }

    @Test
    void fromExisting() {
        String stringSource = "foo";
        Landscape existing = new Landscape(
                "foobar", Map.of("agroup", new Group("agroup", "foobar")),
                "foobar", null, null, null, new Source(stringSource), null, null, Collections.emptyMap()
        );
        SeedConfiguration configuration = new SeedConfiguration("foobar");
        when(configurationFactory.fromString(eq(stringSource), any(Source.class))).thenReturn(configuration);

        //when
        dispatcher.fromExistingLandscape(existing);

        //then
        verify(configurationFactory).fromString(eq(stringSource), any(Source.class));
        verify(publisher).publishEvent(eventCaptor.capture());
        IndexEvent value = eventCaptor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getSeedConfiguration()).isPresent().get().isEqualTo(configuration);
    }
}