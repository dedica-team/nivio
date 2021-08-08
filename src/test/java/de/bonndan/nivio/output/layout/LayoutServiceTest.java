package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.AssessmentChangedEvent;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.output.Renderer;
import de.bonndan.nivio.output.map.RenderingRepository;
import de.bonndan.nivio.output.map.svg.SVGDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LayoutServiceTest {

    private AppearanceProcessor appearanceProcessor;
    private Layouter layouter;
    private RenderingRepository renderingRepository;
    private LayoutService service;
    private Renderer renderer;

    @BeforeEach
    void setUp() {
        appearanceProcessor = mock(AppearanceProcessor.class);
        layouter = mock(Layouter.class);
        renderer = mock(Renderer.class);
        renderingRepository = mock(RenderingRepository.class);
        service = new LayoutService(appearanceProcessor, layouter, renderer, renderingRepository);
    }

    @Test
    void onApplicationEvent() {

        //given
        Landscape landscape = LandscapeFactory.createForTesting("foo", "foo").build();
        Assessment assessment = new Assessment(Map.of());
        AssessmentChangedEvent e = new AssessmentChangedEvent(landscape, assessment);
        when(layouter.layout(eq(landscape))).thenReturn(mock(LayoutedComponent.class));
        when(renderer.render(any(LayoutedComponent.class), eq(assessment), eq(false))).thenReturn(mock(SVGDocument.class));
        when(renderer.getArtefactType()).thenReturn(SVGDocument.class);

        //when
        service.onApplicationEvent(e);

        //then
        verify(appearanceProcessor).process(eq(landscape));
        verify(layouter).layout(eq(landscape));
        verify(renderer).render(any(LayoutedComponent.class), eq(assessment), eq(false));
        verify(renderingRepository).save(eq(SVGDocument.class), eq(landscape), any(SVGDocument.class), eq(false));
    }
}