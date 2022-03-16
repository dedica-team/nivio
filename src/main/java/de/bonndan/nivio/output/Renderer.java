package de.bonndan.nivio.output;

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
     * @param landscape layouted landscape
     * @param options   rendering options, assessment, debug
     * @return the rendering result in the given type
     */
    T render(@NonNull final LayoutedComponent landscape, @NonNull RendererOptions options);

    /**
     * Renders the layouted landscape and writes the result directly into a file.
     *
     * @param landscape layouted landscape
     * @param options   rendering options, assessment, debug
     * @param file      destination file
     * @throws IOException if file cannot be written
     */
    void render(@NonNull final LayoutedComponent landscape,
                @NonNull RendererOptions options,
                @NonNull final File file
    ) throws IOException;

    /**
     * @return the generated class of the impl
     */
    String getRenderingType();
}
