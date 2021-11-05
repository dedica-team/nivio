package de.bonndan.nivio.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.config.ApplicationConfig;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.Indexer;
import de.bonndan.nivio.input.InputFormatHandlerFactory;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.external.LinkHandlerFactory;
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
    protected LandscapeDescriptionFactory factory;
    protected HttpService httpService;
    private ObjectMapper objectMapper;
    private LayoutService layoutService;
    private AppearanceProcessor appearanceProcessor;

    public void setup() throws URISyntaxException {
        landscapeRepository = new LandscapeRepository();
        formatFactory = new InputFormatHandlerFactory(List.of(new InputFormatHandlerNivio(new FileFetcher(new HttpService()))));
        httpService = mock(HttpService.class);
        objectMapper = new ApplicationConfig(null).jackson2ObjectMapperBuilder().build();
        var externalIconsProvider = Mockito.mock(ExternalIconsProvider.class);


        CachedResponse response = mock(CachedResponse.class);
        when(response.getBytes()).thenReturn("foo".getBytes());
        when(httpService.getResponse(any(URL.class))).thenReturn(response);

        FileFetcher fileFetcher = new FileFetcher(httpService);
        factory = new LandscapeDescriptionFactory(fileFetcher);

        LinkHandlerFactory linkHandlerFactory = mock(LinkHandlerFactory.class);
        IconService iconService = new IconService(new LocalIcons(), new ExternalIcons(httpService, externalIconsProvider));

        MapStyleSheetFactory mapStyleSheetFactory = mock(MapStyleSheetFactory.class);
        when(mapStyleSheetFactory.getMapStylesheet(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("");
        appearanceProcessor = new AppearanceProcessor(new IconService(new LocalIcons(""), new ExternalIcons(httpService, externalIconsProvider)));
        layoutService = new LayoutService(
                new AppearanceProcessor(iconService),
                new OrganicLayouter(),
                new SVGRenderer(mapStyleSheetFactory),
                new RenderingRepository(),
                mock(ApplicationEventPublisher.class)
        );
        indexer = new Indexer(landscapeRepository, formatFactory, linkHandlerFactory, mock(ApplicationEventPublisher.class));
    }

    protected Landscape getLandscape(String path) {
        File file = new File(RootPath.get() + path);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        indexer.index(landscapeDescription);

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeDescription.getIdentifier()).orElseThrow();
        appearanceProcessor.process(landscape);
        return landscape;
    }

    protected LayoutedComponent debugRenderLandscape(String path, Landscape landscape) throws IOException {

        LayoutedComponent graph = layoutService.layout(landscape);
        toSVG(landscape, new Assessment(landscape.applyKPIs(landscape.getKpis())), RootPath.get() + path);
        return graph;
    }

    protected String renderLandscape(String path, Landscape landscape) throws IOException {
        return toSVG(landscape, new Assessment(landscape.applyKPIs(landscape.getKpis())), RootPath.get() + path);
    }

    private String toSVG(Landscape landscape, Assessment assessment, String filename) throws IOException {

        File json = new File(filename + "_debug.json");
        objectMapper.writeValue(json, layoutService.layout(landscape));

        String xml = (String) layoutService.render(landscape, assessment, true);

        File svgFile = new File(filename + "_debug.svg");
        FileWriter fileWriter = new FileWriter(svgFile);
        fileWriter.write(xml);
        fileWriter.close();
        return xml;
    }

}
