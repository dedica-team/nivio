package de.bonndan.nivio.output;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import org.springframework.lang.NonNull;

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
     * @param landscape  layouted landscape
     * @param assessment landscape assessment
     * @param debug      if true, result may contain extra elements to show internal rendering details
     * @return the rendering result in the given type
     */
    T render(@NonNull final LayoutedComponent landscape, @NonNull final Assessment assessment, boolean debug);

    /**
     * Renders the layouted landscape and writes the result directly into a file.
     *
     * @param landscape  layouted landscape
     * @param assessment landscape assessment
     * @param file       destination file
     * @param debug      if true, result may contain extra elements to show internal rendering details
     * @throws IOException if file cannot be written
     */
    void render(@NonNull final LayoutedComponent landscape, @NonNull final Assessment assessment, @NonNull final File file, boolean debug) throws IOException;
}
