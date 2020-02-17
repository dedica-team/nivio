package de.bonndan.nivio.output;

import de.bonndan.nivio.model.LandscapeImpl;

import java.io.File;
import java.io.IOException;

/**
 * Any class that can transform a landscape into some sort of output.
 *
 * @param <T>
 */
public interface Renderer<T> {

    T render(LandscapeImpl landscape);

    void render(LandscapeImpl landscape, File file) throws IOException;
}
