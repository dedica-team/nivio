package de.bonndan.nivio.output;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;

import java.util.Map;

/**
 * Finally rendered output.
 *
 * @param <I> the object type in the rendering
 */
public interface LayoutedArtifact<I> {

    /**
     * The rendered object.
     *
     * @return the finally rendered implementation
     */
    I getRendered();
}
