package de.bonndan.nivio.output.map;

import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.RenderedArtifact;

/**
 * A class that can transform a rendered landscape into a map.
 *
 * @param <T> graph impl
 * @param <R> graph item impl
 */
public interface MapFactory<T,R> {

    /**
     * Applies values from a rendering to landscape components.
     *
     */
    void applyArtifactValues(LandscapeImpl landscape, RenderedArtifact<T, R> renderedArtifact);
}
