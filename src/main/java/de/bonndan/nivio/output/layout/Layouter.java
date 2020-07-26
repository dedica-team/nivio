package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.LandscapeImpl;

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
    LayoutedComponent layout(LandscapeImpl landscape);
}
