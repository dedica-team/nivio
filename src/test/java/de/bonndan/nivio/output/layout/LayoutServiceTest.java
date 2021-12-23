package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.output.Renderer;
import de.bonndan.nivio.output.map.RenderingRepository;
import de.bonndan.nivio.output.map.svg.SVGDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LayoutServiceTest {

    private AppearanceProcessor appearanceProcessor;
    private Layouter layouter;
    private RenderingRepository renderingRepository;
    private LayoutService service;
    private Renderer renderer;
    private ApplicationEventPublisher eventPublisher;
    private Landscape landscape;

    @BeforeEach
    void setUp() {
        appearanceProcessor = mock(AppearanceProcessor.class);
        layouter = mock(Layouter.class);
        renderer = mock(Renderer.class);
        renderingRepository = mock(RenderingRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new LayoutService(appearanceProcessor, layouter, renderer, renderingRepository, eventPublisher);
         landscape = LandscapeFactory.createForTesting("foo", "foo").build();
    }

    @Test
    @DisplayName("Fires layout change event")
    void onApplicationEvent() {

        //given
        LandscapeDescription landscapeDescription = new LandscapeDescription("foo");
        ProcessingChangelog changelog = new ProcessingChangelog();
        changelog.addEntry(landscape, ProcessingChangelog.ChangeType.CREATED);
        ProcessingFinishedEvent e = new ProcessingFinishedEvent(landscapeDescription, landscape, changelog);
        when(layouter.layout(eq(landscape))).thenReturn(mock(LayoutedComponent.class));
        when(renderer.render(any(LayoutedComponent.class), eq(null), eq(false))).thenReturn(mock(SVGDocument.class));
        when(renderer.getRenderingType()).thenReturn(SVGDocument.class.getSimpleName());

        //when
        service.onApplicationEvent(e);

        //then
        verify(appearanceProcessor).process(eq(landscape));
        verify(layouter).layout(eq(landscape));
        verify(renderer).render(any(LayoutedComponent.class), eq(null), eq(false));
        verify(renderingRepository).save(eq(SVGDocument.class.getSimpleName()), eq(landscape), any(SVGDocument.class), eq(false));
        verify(eventPublisher).publishEvent(any(LayoutChangedEvent.class));
    }

    @Test
    @DisplayName("Does not fire layout change event")
    void doesNotFire() {

        //given
        LandscapeDescription landscapeDescription = new LandscapeDescription("foo");
        ProcessingChangelog changelog = new ProcessingChangelog();
        changelog.addEntry(landscape, ProcessingChangelog.ChangeType.UPDATED);
        ProcessingFinishedEvent e = new ProcessingFinishedEvent(landscapeDescription, landscape, changelog);
        when(layouter.layout(eq(landscape))).thenReturn(mock(LayoutedComponent.class));
        when(renderer.render(any(LayoutedComponent.class), eq(null), eq(false))).thenReturn(mock(SVGDocument.class));
        when(renderer.getRenderingType()).thenReturn(SVGDocument.class.getSimpleName());

        //when
        service.onApplicationEvent(e);

        //then
        verify(appearanceProcessor).process(eq(landscape));
        verify(layouter).layout(eq(landscape));
        verify(renderer).render(any(LayoutedComponent.class), eq(null), eq(false));
        verify(renderingRepository).save(eq(SVGDocument.class.getSimpleName()), eq(landscape), any(SVGDocument.class), eq(false));
        verify(eventPublisher, never()).publishEvent(any(LayoutChangedEvent.class));
    }
}