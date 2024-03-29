package de.bonndan.nivio.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.IntegrationTestSupport;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.*;
import de.bonndan.nivio.input.http.CachedResponse;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.nivio.InputFormatHandlerNivio;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.output.icons.ExternalIcons;
import de.bonndan.nivio.output.icons.ExternalIconsProvider;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.output.icons.LocalIcons;
import de.bonndan.nivio.output.layout.AppearanceProcessor;
import de.bonndan.nivio.output.layout.LayoutService;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.layout.OrganicLayouter;
import de.bonndan.nivio.output.map.RenderingRepository;
import de.bonndan.nivio.output.map.svg.MapStyleSheetFactory;
import de.bonndan.nivio.output.map.svg.SVGRenderer;
import de.bonndan.nivio.util.RootPath;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Almost an integration test. Provides everything to transform a yaml into an svg.
 */
public abstract class RenderingTest {

    protected LandscapeRepository landscapeRepository;
    protected InputFormatHandlerFactory formatFactory;
    protected Indexer indexer;
    protected SeedConfigurationFactory factory;
    protected HttpService httpService;
    private ObjectMapper objectMapper;
    private LayoutService layoutService;
    private AppearanceProcessor appearanceProcessor;
    private IntegrationTestSupport integrationTestSupport;

    public void setup() throws URISyntaxException {
        integrationTestSupport = new IntegrationTestSupport();
        formatFactory = new InputFormatHandlerFactory(List.of(new InputFormatHandlerNivio(new FileFetcher(new HttpService()))));
        httpService = integrationTestSupport.getHttpService();
        objectMapper = integrationTestSupport.getObjectMapper();
        landscapeRepository = integrationTestSupport.getLandscapeRepository();

        CachedResponse response = mock(CachedResponse.class);
        when(response.getBytes()).thenReturn("foo".getBytes());
        when(httpService.getResponse(any(URL.class))).thenReturn(response);

        factory = integrationTestSupport.getSeedConfigurationFactory();
        indexer = integrationTestSupport.getIndexer();

        /*
         rendering stuff
         */
        ExternalIcons externalIcons = integrationTestSupport.getExternalIcons();
        IconService iconService = new IconService(new LocalIcons(), externalIcons);

        MapStyleSheetFactory mapStyleSheetFactory = mock(MapStyleSheetFactory.class);
        when(mapStyleSheetFactory.getMapStylesheet(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("");
        appearanceProcessor = new AppearanceProcessor(iconService);
        layoutService = new LayoutService(
                appearanceProcessor,
                new OrganicLayouter(),
                new SVGRenderer(mapStyleSheetFactory),
                new RenderingRepository(),
                mock(ApplicationEventPublisher.class)
        );

    }

    protected Landscape getLandscape(String path) {
        File file = new File(RootPath.get() + path);
        Landscape landscape1 = integrationTestSupport.getFirstIndexedLandscape(file);
        appearanceProcessor.process(landscape1);
        return landscape1;
    }

    protected LayoutedComponent debugRenderLandscape(String path, Landscape landscape) throws IOException {

        OrganicLayouter organicLayouter = new OrganicLayouter(true);
        LayoutedComponent graph = organicLayouter.layout(landscape);
        toSVG(graph, new Assessment(landscape.applyKPIs(landscape.getKpis())), RootPath.get() + path);
        return graph;
    }

    protected String renderLandscape(String path, Landscape landscape) throws IOException {
        LayoutedComponent graph = layoutService.layout(landscape);
        return toSVG(graph, new Assessment(landscape.applyKPIs(landscape.getKpis())), RootPath.get() + path);
    }

    private String toSVG(LayoutedComponent graph, Assessment assessment, String filename) throws IOException {

        File json = new File(filename + "_debug.json");
        objectMapper.writeValue(json, graph);

        String xml = (String) layoutService.render(graph, assessment, true);

        File svgFile = new File(filename + "_debug.svg");
        FileWriter fileWriter = new FileWriter(svgFile);
        fileWriter.write(xml);
        fileWriter.close();
        return xml;
    }

}
