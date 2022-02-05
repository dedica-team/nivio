package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.AssessmentFactory;
import de.bonndan.nivio.assessment.kpi.ConditionKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.GroupBuilder;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.util.FrontendMapping;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GroupingReportGeneratorTest {

    private GroupingReportGenerator groupingReportGenerator;
    private ConditionKPI conditionKpi;
    private Map<String, KPI> map;
    private AssessmentFactory factory;
    private FrontendMapping frontendMapping;
    private Group testGroup;
    private GraphTestSupport graph;

    @BeforeEach
    void setUp() {
        groupingReportGenerator = new GroupingReportGenerator(Mockito.mock(LocalServer.class), Mockito.mock(IconService.class));
        conditionKpi = new ConditionKPI();
        map = new HashMap<>();
        map.put("test", conditionKpi);
        factory = new AssessmentFactory();
        frontendMapping = Mockito.mock(FrontendMapping.class);
        Mockito.when(frontendMapping.getKeys()).thenReturn(Map.of());

        graph = new GraphTestSupport();
        testGroup = graph.getTestGroup("nivio");

        URI uri = URI.create("https://www.nivio.com/");
        String[] tags = Arrays.array("auth", "ui");
        Item foo = graph.getTestItemBuilder(testGroup.getIdentifier(), "nivio").withParent(testGroup).withAddress(uri).build();
        foo.setTags(tags);
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(foo);
    }

    @Test
    void toDocumentOwners() {
        // given

        var assessment = factory.createAssessment(graph.landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"owners"}));

        //  when
        String document = groupingReportGenerator.toDocument(graph.landscape, assessment, searchConfig, frontendMapping);

        // then
        assertThat(document).contains("Date: ")
                .contains(searchConfig.getTitle())
                .contains("Address: https://www.nivio.com/")
                .contains("Tags: auth, ui")
                .contains("<h2>Owners: domain</h2>");
    }

    @Test
    void toDocumentGroups() {
        // given
        var assessment = factory.createAssessment(graph.landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"groups"}));

        //  when
        String document = groupingReportGenerator.toDocument(graph.landscape, assessment, searchConfig, frontendMapping);

        // then
        assertThat(document).contains("Date: ")
                .contains(searchConfig.getTitle())
                .contains("Address: https://www.nivio.com/")
                .contains("Tags: auth, ui")
                .contains("<h2>Groups: nivio</h2>");

    }

    @Test
    void toDocumentLifecycle() {
        // given
        var assessment = factory.createAssessment(graph.landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"lifecycle"}));

        //  when
        String document = groupingReportGenerator.toDocument(graph.landscape, assessment, searchConfig, frontendMapping);

        // then
        assertThat(document).contains("Date: ")
                .contains(searchConfig.getTitle())
                .contains("Address: https://www.nivio.com/")
                .contains("Tags: auth, ui")
                .contains("<h2>Lifecycle: PRODUCTION</h2>");

    }

    @Test
    void toDocumentKpi() {
        // given
        var assessment = factory.createAssessment(graph.landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"kpis"}));

        //  when
        String document = groupingReportGenerator.toDocument(graph.landscape, assessment, searchConfig, frontendMapping);

        // then
        assertThat(document).contains("Date: ")
                .contains(searchConfig.getTitle())
                .contains("Address: https://www.nivio.com/")
                .contains("Tags: auth, ui")
                .contains("<h2>KPIs: grey</h2>");

    }

    @Test
    void toDocumentSearch() {
        // given
        var assessment = factory.createAssessment(graph.landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"owners"}, "searchTerm", new String[]{"ownerz"}));

        //items need to be indexed before the search can execute
        graph.landscape.getIndexReadAccess().indexForSearch(new Assessment(new HashMap<>()));

        //  when
        String document = groupingReportGenerator.toDocument(graph.landscape, assessment, searchConfig, frontendMapping);

        // then
        assertThat(document).contains("Date: ")
                .contains(searchConfig.getTitle())
                .doesNotContain("Address: https://www.nivio.com/")
                .doesNotContain("Tags: auth, ui")
                .doesNotContain("<h2>Owners: common</h2>");

    }

    @Test
    void toDocumentMapping() {
        // given
        Mockito.when(frontendMapping.getKeys()).thenReturn(Map.of("Owners", "test"));
        var assessment = factory.createAssessment(graph.landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"owners"}));

        //  when
        String document = groupingReportGenerator.toDocument(graph.landscape, assessment, searchConfig, frontendMapping);

        // then
        assertThat(document).contains("Date: ")
                .contains(searchConfig.getTitle())
                .contains("Address: https://www.nivio.com/")
                .contains("Tags: auth, ui")
                .contains("<h2>test: domain</h2>");

    }

    @Test
    void toDocumentNoSearchConfig() {
        // given
        var assessment = factory.createAssessment(graph.landscape, map);

        //  when
        String document = groupingReportGenerator.toDocument(graph.landscape, assessment, null, frontendMapping);

        // then
        assertThat(document).contains("Date: ")
                .contains("Report")
                .doesNotContain("Address: https://www.nivio.com/")
                .doesNotContain("Tags: auth, ui")
                .doesNotContain("<h2>Owners: domain</h2>");

    }
}