package de.bonndan.nivio.output.map;

import de.bonndan.nivio.output.Rendered;

/**
 * A class that can transform a rendered landscape into a map.
 *
 * @param <T> graph impl
 * @param <R> graph item impl
 */
public interface MapFactory<T,R> {

    RenderedXYMap getRenderedMap(Rendered<T,R> rendered);
}
