package de.bonndan.nivio.output;

import de.bonndan.nivio.model.LandscapeImpl;

import java.io.File;
import java.io.IOException;

public interface Renderer<T> {

    T render(LandscapeImpl landscape);

    void render(LandscapeImpl landscape, File file) throws IOException;
}
