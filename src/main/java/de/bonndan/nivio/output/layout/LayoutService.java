package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.AssessmentChangedEvent;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.Renderer;
import de.bonndan.nivio.output.map.RenderingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Service to generate the graphical appearance of a landscape.
 *
 * Appearance must be determined after indexing, because values might be needed in api, too.
 */
@Service
public class LayoutService implements ApplicationListener<AssessmentChangedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutService.class);

    private final AppearanceProcessor appearanceProcessor;
    private final Layouter layouter;
    private final Renderer<?> renderer;
    private final RenderingRepository renderingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public LayoutService(final AppearanceProcessor appearanceProcessor,
                         final Layouter layouter,
                         final Renderer<?> renderer,
                         final RenderingRepository renderingRepository,
                         final ApplicationEventPublisher eventPublisher
    ) {
        this.appearanceProcessor = appearanceProcessor;
        this.layouter = layouter;
        this.renderer = renderer;
        this.renderingRepository = renderingRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onApplicationEvent(@NonNull final AssessmentChangedEvent event) {
        Landscape landscape = event.getLandscape();
        LOGGER.info("Calculating layout for landscape {}", landscape);
        appearanceProcessor.process(landscape);
        Assessment assessment = event.getAssessment();
        LayoutedComponent layout = layout(landscape);

        var debug = false;
        var artefact = renderer.render(layout, assessment, debug);
        renderingRepository.save(renderer.getRenderingType(), landscape, artefact, debug);
        LOGGER.info("Generated {} rendering of landscape {} (debug: {})", renderer.getRenderingType(), landscape.getIdentifier(), debug);
        eventPublisher.publishEvent(new LayoutChangedEvent(landscape, "Rendered landscape " + landscape.getIdentifier()));
    }

    public LayoutedComponent layout(@NonNull final Landscape landscape) {
        return layouter.layout(Objects.requireNonNull(landscape));
    }

    /**
     * Layouts and renders the given landscape.
     *
     * @param landscape  landscape
     * @param assessment the related assessment
     * @param debug      flag to add debug info
     * @return renderer artefact
     */
    public Object render(@NonNull final Landscape landscape, @NonNull final Assessment assessment, boolean debug) {
        return renderer.render(layout(landscape), Objects.requireNonNull(assessment), debug);
    }
}
