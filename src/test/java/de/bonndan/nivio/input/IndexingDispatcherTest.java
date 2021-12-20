package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.Source;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.observation.InputChangedEvent;
import de.bonndan.nivio.observation.ObservedChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.net.MalformedURLException;
import java.net.URL;
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
    private SourceReferencesResolver resolver;

    @BeforeEach
    void setUp() {
        configurationFactory = mock(SeedConfigurationFactory.class);
        landscapeDescriptionFactory = mock(LandscapeDescriptionFactory.class);
        publisher = mock(ApplicationEventPublisher.class);
        eventCaptor = ArgumentCaptor.forClass(IndexEvent.class);
        resolver = mock(SourceReferencesResolver.class);

        dispatcher = new IndexingDispatcher(configurationFactory, landscapeDescriptionFactory, publisher, resolver);
    }

    @Test
    void createLandscapeDescriptionFromBody() {

        LandscapeDescription dto = new LandscapeDescription("foo");
        when(landscapeDescriptionFactory.fromString(eq("foo"), anyString())).thenReturn(dto);

        //when
        dispatcher.updateLandscapeDescriptionFromBody("foo", "foo");

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

    @Test
    void onSeedConfigurationChangeEvent() {
        SeedConfiguration configuration = new SeedConfiguration("foobar");
        SeedConfigurationChangeEvent event = new SeedConfigurationChangeEvent(configuration, "");

        //when
        dispatcher.onSeedConfigurationChangeEvent(event);

        verify(resolver).resolve(configuration);
        verify(publisher).publishEvent(any(IndexEvent.class));
    }

    @Test
    void onInputChangedEvent() throws MalformedURLException {
        SeedConfiguration configuration = new SeedConfiguration("foobar");
        InputChangedEvent event = new InputChangedEvent(new URL("http://foo.com"), new ObservedChange("test"));
        when(configurationFactory.from(any(URL.class))).thenReturn(configuration);

        //when
        dispatcher.onInputChangedEvent(event);

        //then
        verify(resolver).resolve(configuration);
        verify(configurationFactory).from(any(URL.class));
        verify(publisher).publishEvent(any(IndexEvent.class));
    }
}