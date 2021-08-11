package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.Renderer;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Turns the layouted landscape into a SVG image.
 */
@Service
public class SVGRenderer implements Renderer<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SVGRenderer.class);

    public static final int DEFAULT_ICON_SIZE = 50;
    public static final String RENDERING_TYPE = "svg";

    private final MapStyleSheetFactory mapStyleSheetFactory;

    public SVGRenderer(MapStyleSheetFactory mapStyleSheetFactory) {
        this.mapStyleSheetFactory = mapStyleSheetFactory;
    }

    @Override
    public String render(@NonNull final LayoutedComponent landscape, @Nullable final Assessment assessment, boolean debug) {
        SVGDocument svgDocument = new SVGDocument(landscape, assessment, getStyles((Landscape) landscape.getComponent()));
        svgDocument.setDebug(debug);
        return svgDocument.getXML();
    }

    @Override
    public void render(@NonNull final LayoutedComponent landscape,
                       @NonNull final Assessment assessment,
                       @NonNull final File file, boolean debug
    ) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(render(landscape, assessment, debug));
        } catch (IOException e) {
            LOGGER.error("Failed to render to file", e);
        }
    }

    @Override
    public String getRenderingType() {
        return RENDERING_TYPE;
    }

    private String getStyles(Landscape landscape) {
        String css = "";
        try (InputStream resourceAsStream = getClass().getResourceAsStream("/static/css/svg.css")) {
            css = new String(StreamUtils.copyToByteArray(resourceAsStream));
        } catch (IOException e) {
            LOGGER.error("Failed to load stylesheet /static/css/svg.css");
        }

        return css + "\n" + mapStyleSheetFactory.getMapStylesheet(landscape.getConfig(), landscape.getLog());
    }

}
