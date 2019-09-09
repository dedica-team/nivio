package de.bonndan.nivio.output;

import de.bonndan.nivio.landscape.Landscape;

import java.io.File;
import java.io.IOException;

public interface Renderer<T> {

    T render(Landscape landscape);

    void render(Landscape landscape, File file) throws IOException;
}
