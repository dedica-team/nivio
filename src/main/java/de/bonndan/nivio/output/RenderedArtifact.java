package de.bonndan.nivio.output;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;

import java.util.Map;

/**
 * Finally rendered output.
 *
 * @param <M> the renderer implementation
 * @param <I> the object type in the rendering
 */
public interface RenderedArtifact<M, I> {

    /**
     * The rendered object.
     *
     * @return the finally rendered implementation
     */
    M getRendered();

    /**
     * A map of landscape items and their representation.
     *
     * @return rendered object (dependends on the rendered implementation)
     */
    Map<Item, I> getItemObjects();

    /**
     * A map of landscape group and their representation.
     *
     * @return rendered object (dependends on the rendered implementation)
     */
    Map<Group, I> getGroupObjects();
}
