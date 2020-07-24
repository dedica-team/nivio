package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.LayoutedArtifact;

/**
 * Layouts / arranges landscapes.
 *
 * @param <T> type where layout values are stored
 */
public interface Layouter<T> {

    /**
     * Arranges the landscape components.
     *
     * @param landscape landscape impl
     * @return an artifact
     */
    LayoutedArtifact<T> layout(LandscapeImpl landscape);
}
