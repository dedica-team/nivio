package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.Renderer;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class SVGRenderer implements Renderer<String> {

    public static final int DEFAULT_ICON_SIZE = 50;
    private final MapStyleSheetFactory mapStyleSheetFactory;

    public SVGRenderer(MapStyleSheetFactory mapStyleSheetFactory) {
        this.mapStyleSheetFactory = mapStyleSheetFactory;
    }

    @Override
    public String render(LandscapeImpl landscape) {
        SVGDocument svgDocument = new SVGDocument(landscape, mapStyleSheetFactory);
        return svgDocument.getXML();
    }

    @Override
    public void render(LandscapeImpl landscape, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(render(landscape));
        fileWriter.close();
    }
}
