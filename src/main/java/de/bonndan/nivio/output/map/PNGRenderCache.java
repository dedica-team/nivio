package de.bonndan.nivio.output.map;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.RenderedArtifact;
import de.bonndan.nivio.output.jgraphx.JGraphXRenderer;
import de.bonndan.nivio.output.map.svg.MapStyleSheetFactory;
import de.bonndan.nivio.output.map.svg.SvgFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A service that caches expensive batik svg to png rendering.
 *
 */
@Service
public class PNGRenderCache implements ApplicationListener<ProcessingFinishedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PNGRenderCache.class);

    private final Map<String, Pair<LandscapeImpl, byte[]>> renderings = new HashMap<>();

    private final MapFactory<mxGraph, mxCell> mapFactory;
    private final MapStyleSheetFactory mapStyleSheetFactory;

    public PNGRenderCache(MapFactory<mxGraph, mxCell> mapFactory, MapStyleSheetFactory mapStyleSheetFactory) {
        this.mapFactory = mapFactory;
        this.mapStyleSheetFactory = mapStyleSheetFactory;
    }

    /**
     * Returns a png.
     *
     * @param landscape the landscape to render
     * @return the png as byte array, cached if available
     */
    public byte[] getPNG(LandscapeImpl landscape) {

        Pair<LandscapeImpl, byte[]> landscapePair = renderings.get(landscape.getIdentifier());
        if (landscapePair == null || landscapePair.getLeft().getLog().getLastUpdate() != landscape.getLog().getLastUpdate()) {
            byte[] rendered = asByteArray(landscape);
            renderings.put(landscape.getIdentifier(), new ImmutablePair<>(landscape, rendered));
            return rendered;
        }

        return landscapePair.getRight();
    }

    /**
     * Returns an svg.
     *
     * @param landscape the landscape to render
     * @return the svg as string, uncached
     */
    public String getSVG(LandscapeImpl landscape) {
        JGraphXRenderer jGraphXRenderer = new JGraphXRenderer();
        RenderedArtifact<mxGraph, mxCell> render = jGraphXRenderer.render(landscape);
        mapFactory.applyArtifactValues(landscape, render);
        if (landscape.getLog() == null) {
            ProcessLog processLog = new ProcessLog(LOGGER);
            processLog.setLandscape(landscape);
            landscape.setProcessLog(processLog);
        }
        SvgFactory svgFactory = new SvgFactory(landscape, mapStyleSheetFactory);
        return svgFactory.getXML();
    }

    @Override
    public void onApplicationEvent(ProcessingFinishedEvent processingFinishedEvent) {
        Landscape landscape = processingFinishedEvent.getLandscape();
        if (landscape instanceof LandscapeImpl) {
            LOGGER.info("Generating PNG rendering of landscape {}", landscape.getIdentifier());
            getPNG((LandscapeImpl) landscape);
        }
    }

    private byte[] asByteArray(LandscapeImpl landscape) {
        TranscoderInput input_svg_image = new TranscoderInput(new ByteArrayInputStream(getSVG(landscape).getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
        Transcoder transcoder = new PNGTranscoder();
        try {
            transcoder.transcode(input_svg_image, transcoderOutput);
        } catch (TranscoderException e) {
            throw new RuntimeException("Failed to create PNG", e);
        }
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to render landscape " + landscape.getIdentifier(), e);
        }

        return outputStream.toByteArray();
    }
}
