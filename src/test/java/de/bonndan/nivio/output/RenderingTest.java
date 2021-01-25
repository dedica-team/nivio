package de.bonndan.nivio.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.*;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.external.LinkHandlerFactory;
import de.bonndan.nivio.input.nivio.InputFormatHandlerNivio;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.output.icons.LocalIcons;
import de.bonndan.nivio.output.icons.ExternalIcons;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.layout.OrganicLayouter;
import de.bonndan.nivio.output.map.svg.MapStyleSheetFactory;
import de.bonndan.nivio.output.map.svg.SVGRenderer;
import de.bonndan.nivio.util.RootPath;
import org.mockito.ArgumentMatchers;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Almost an integration test. Provides everything to transform a yaml into an svg.
 */
public abstract class RenderingTest {

    protected LandscapeRepository landscapeRepository;
    protected InputFormatHandlerFactory formatFactory;
    protected Indexer indexer;
    protected LandscapeDescriptionFactory factory;

    public void setup() {
        landscapeRepository = new LandscapeRepository();
        formatFactory = InputFormatHandlerFactory.with(new InputFormatHandlerNivio(new FileFetcher(new HttpService())));
        FileFetcher fileFetcher = new FileFetcher(mock(HttpService.class));
        factory = new LandscapeDescriptionFactory(fileFetcher);

        HttpService httpService = mock(HttpService.class);
        LinkHandlerFactory linkHandlerFactory = mock(LinkHandlerFactory.class);
        IconService iconService = new IconService(new LocalIcons(), new ExternalIcons(httpService));
        indexer = new Indexer(landscapeRepository, formatFactory, linkHandlerFactory, mock(ApplicationEventPublisher.class), iconService);
    }

    protected Landscape getLandscape(String path) {
        File file = new File(RootPath.get() + path);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        indexer.index(landscapeDescription);
        return landscapeRepository.findDistinctByIdentifier(landscapeDescription.getIdentifier()).orElseThrow();
    }

    protected LayoutedComponent debugRenderLandscape(String path, Landscape landscape) throws IOException {

        OrganicLayouter layouter = new OrganicLayouter();
        LayoutedComponent graph = layouter.layout(landscape);
        toSVG(graph, RootPath.get() + path);
        return graph;
    }

    protected String renderLandscape(String path, Landscape landscape) throws IOException {

        OrganicLayouter layouter = new OrganicLayouter();
        LayoutedComponent graph = layouter.layout(landscape);
        return toSVG(graph, RootPath.get() + path);
    }

    private String toSVG(LayoutedComponent layoutedComponent, String filename) throws IOException {

        MapStyleSheetFactory mapStyleSheetFactory = mock(MapStyleSheetFactory.class);
        when(mapStyleSheetFactory.getMapStylesheet(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("");

        File json = new File(filename + "_debug.json");
        new ObjectMapper().writeValue(json, layoutedComponent);

        SVGRenderer svgRenderer = new SVGRenderer(mapStyleSheetFactory);
        String svg = svgRenderer.render(layoutedComponent, true);

        File svgFile = new File(filename + "_debug.svg");
        FileWriter fileWriter = new FileWriter(svgFile);
        fileWriter.write(svg);
        fileWriter.close();

        return svg;
    }

}
