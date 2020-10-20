package de.bonndan.nivio.output;

import de.bonndan.nivio.output.layout.LayoutedComponent;

import java.io.File;
import java.io.IOException;

/**
 * Any class that can transform a landscape into some sort of output.
 *
 * @param <T> output type
 */
public interface Renderer<T> {

    /**
     * Renders a landscape layout to the given type.
     *
     * @param landscape layouted landscape
     * @param debug             if true, result may contain extra elements to show internal rendering details
     * @return the rendering result in the given type
     */
    T render(LayoutedComponent landscape, boolean debug);

    /**
     * Renders the layouted landscape and writes the result directly into a file.
     *
     * @param landscape layouted landscape
     * @param file      destination file
     * @param debug     if true, result may contain extra elements to show internal rendering details
     * @throws IOException if file cannot be written
     */
    void render(LayoutedComponent landscape, File file, boolean debug) throws IOException;
}
