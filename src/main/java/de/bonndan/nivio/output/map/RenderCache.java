package de.bonndan.nivio.output.map;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.AssessmentRepository;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.layout.Layouter;
import de.bonndan.nivio.output.layout.OrganicLayouter;
import de.bonndan.nivio.output.map.svg.SVGRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * A service that caches map rendering.
 */
@Service
public class RenderCache implements ApplicationListener<ProcessingFinishedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenderCache.class);

    /**
     * cache map, key is FQI string representation (or debugged version)
     */
    private final Map<String, String> renderings = new HashMap<>();

    private final AssessmentRepository assessmentRepository;
    private final Layouter<LayoutedComponent> layouter;
    private final SVGRenderer svgRenderer;

    public RenderCache(final SVGRenderer svgRenderer, AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
        this.svgRenderer = svgRenderer;
        layouter = new OrganicLayouter();
    }

    /**
     * Returns an svg.
     *
     * @param landscape the landscape to render
     * @param debug     flag to enable debug messages
     * @return the svg as string
     */
    @Nullable
    public String getSVG(Landscape landscape, boolean debug) {

        String key = getKey(landscape, debug);
        if (!renderings.containsKey(key)) {
            createCacheEntry(landscape, getAssessment(landscape), debug);
        }

        return renderings.get(key);
    }

    private String getKey(Landscape landscape, boolean debug) {
        return landscape.getFullyQualifiedIdentifier().toString() + (debug ? "debug" : "");
    }

    private void createCacheEntry(Landscape landscape, Assessment assessment, boolean debug) {
        LayoutedComponent layout = layouter.layout(landscape);
        LOGGER.info("Generating SVG rendering of landscape {} (debug: {})", landscape.getIdentifier(), debug);
        renderings.put(getKey(landscape, debug), svgRenderer.render(layout, assessment, debug).getXML());
    }

    @Override
    public void onApplicationEvent(ProcessingFinishedEvent processingFinishedEvent) {
        Landscape landscape = processingFinishedEvent.getLandscape();
        createCacheEntry(landscape, getAssessment(landscape), false);
    }

    private Assessment getAssessment(Landscape landscape) {
        var assessment = assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier());
        if (assessment != null) {
            return assessment;
        } else {
            return assessmentRepository.createAssessment(landscape);
        }
    }
}
