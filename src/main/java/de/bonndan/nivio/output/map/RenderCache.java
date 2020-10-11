package de.bonndan.nivio.output.map;

import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.ProcessLog;
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
 *
 *
 */
@Service
public class RenderCache implements ApplicationListener<ProcessingFinishedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenderCache.class);

    private final Map<Landscape, String> renderings = new HashMap<>();

    private final SVGRenderer svgRenderer;
    private final Layouter<LayoutedComponent> layouter;

    public RenderCache(SVGRenderer svgRenderer) {
        this.svgRenderer = svgRenderer;
        layouter = new OrganicLayouter();
    }


    /**
     * Returns an svg.
     *
     * @param landscape the landscape to render
     * @return the svg as string, uncached
     */
    @Nullable
    public String getSVG(Landscape landscape) {

        if (!renderings.containsKey(landscape))
            createCacheEntry(landscape);

        return renderings.get(landscape);
    }

    private void createCacheEntry(Landscape landscape) {
        LayoutedComponent layout = layouter.layout(landscape);

        if (landscape.getLog() == null) {
            ProcessLog processLog = new ProcessLog(LOGGER);
            processLog.setLandscape(landscape);
            landscape.setProcessLog(processLog);
        }
        renderings.put(landscape, svgRenderer.render(layout));
    }

    @Override
    public void onApplicationEvent(ProcessingFinishedEvent processingFinishedEvent) {
        Landscape landscape = processingFinishedEvent.getLandscape();
        if (landscape != null) {
            LOGGER.info("Generating SVG rendering of landscape {}", landscape.getIdentifier());
            createCacheEntry(landscape);
        }
    }
}
