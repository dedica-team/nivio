package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.assessment.AssessmentFactory;
import de.bonndan.nivio.assessment.kpi.ConditionKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.ItemBuilder;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.LandscapeFactory;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GroupingReportGeneratorTest {

    private GroupingReportGenerator groupingReportGenerator;
    private ConditionKPI conditionKpi;
    private Map<String, KPI> map;
    private FrontendMapping frontendMapping;

    @BeforeEach
    void setUp() {
        groupingReportGenerator = new GroupingReportGenerator(Mockito.mock(LocalServer.class), Mockito.mock(IconService.class));
        conditionKpi = new ConditionKPI();
        map = new HashMap<>();
        frontendMapping = Mockito.mock(FrontendMapping.class);

    }

    @Test
    void toDocumentOwners() {
        // given
        Mockito.when(frontendMapping.getKeys()).thenReturn(Map.of());
        map.put("test", conditionKpi);
        URI uri = URI.create("https://www.nivio.com/");
        String[] tags = Arrays.array("auth", "ui");
        var landscape = LandscapeFactory.createForTesting("test", "test").build();
        Item foo = ItemBuilder.anItem().withLandscape(landscape).withIdentifier("nivio").withGroup("nivio").withAddress(uri).build();
        foo.setTags(tags);
        landscape.setItems(Set.of(foo));
        var assessment = AssessmentFactory.createAssessment(landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"owners"}));

        //  when
        String document = groupingReportGenerator.toDocument(landscape, assessment, searchConfig, frontendMapping);

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
        Mockito.when(frontendMapping.getKeys()).thenReturn(Map.of());
        map.put("test", conditionKpi);
        URI uri = URI.create("https://www.nivio.com/");
        String[] tags = Arrays.array("auth", "ui");
        var landscape = LandscapeFactory.createForTesting("test", "test").build();
        Item foo = ItemBuilder.anItem().withLandscape(landscape).withIdentifier("nivio").withGroup("nivio").withAddress(uri).build();
        foo.setTags(tags);
        landscape.setItems(Set.of(foo));
        var assessment = AssessmentFactory.createAssessment(landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"groups"}));

        //  when
        String document = groupingReportGenerator.toDocument(landscape, assessment, searchConfig, frontendMapping);

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
        Mockito.when(frontendMapping.getKeys()).thenReturn(Map.of());
        map.put("test", conditionKpi);
        URI uri = URI.create("https://www.nivio.com/");
        String[] tags = Arrays.array("auth", "ui");
        var landscape = LandscapeFactory.createForTesting("test", "test").build();
        Item foo = ItemBuilder.anItem().withLandscape(landscape).withIdentifier("nivio").withGroup("nivio").withAddress(uri).withLabels(Map.of("lifecycle", "PRODUCTION")).build();
        foo.setTags(tags);
        landscape.setItems(Set.of(foo));
        var assessment = AssessmentFactory.createAssessment(landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"lifecycle"}));

        //  when
        String document = groupingReportGenerator.toDocument(landscape, assessment, searchConfig, frontendMapping);

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
        Mockito.when(frontendMapping.getKeys()).thenReturn(Map.of());
        map.put("test/nivio/nivio", conditionKpi);
        URI uri = URI.create("https://www.nivio.com/");
        String[] tags = Arrays.array("auth", "ui");
        var landscape = LandscapeFactory.createForTesting("test", "test").build();
        Item foo = ItemBuilder.anItem().withLandscape(landscape).withIdentifier("nivio").withGroup("nivio").withAddress(uri).withLabels(Map.of(Label._condition + ".test", "true")).build();
        foo.setTags(tags);
        landscape.setItems(Set.of(foo));
        var assessment = AssessmentFactory.createAssessment(landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"kpis"}));

        //  when
        String document = groupingReportGenerator.toDocument(landscape, assessment, searchConfig, frontendMapping);

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
        Mockito.when(frontendMapping.getKeys()).thenReturn(Map.of());
        map.put("test/nivio/nivio", conditionKpi);
        URI uri = URI.create("https://www.nivio.com/");
        String[] tags = Arrays.array("auth", "ui");
        var landscape = LandscapeFactory.createForTesting("test", "test").build();
        Item foo = ItemBuilder.anItem().withLandscape(landscape).withIdentifier("nivio").withGroup("nivio").withAddress(uri).build();
        foo.setTags(tags);
        landscape.setItems(Set.of(foo));
        var assessment = AssessmentFactory.createAssessment(landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"owners"}, "searchTerm", new String[]{"xyz"}));

        //  when
        String document = groupingReportGenerator.toDocument(landscape, assessment, searchConfig, frontendMapping);

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
        map.put("test/nivio/nivio", conditionKpi);
        URI uri = URI.create("https://www.nivio.com/");
        String[] tags = Arrays.array("auth", "ui");
        var landscape = LandscapeFactory.createForTesting("test", "test").build();
        Item foo = ItemBuilder.anItem().withLandscape(landscape).withIdentifier("nivio").withGroup("nivio").withAddress(uri).build();
        foo.setTags(tags);
        landscape.setItems(Set.of(foo));
        var assessment = AssessmentFactory.createAssessment(landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{"owners"}));

        //  when
        String document = groupingReportGenerator.toDocument(landscape, assessment, searchConfig, frontendMapping);

        // then
        assertThat(document).contains("Date: ")
                .contains(searchConfig.getTitle())
                .contains("Address: https://www.nivio.com/")
                .contains("Tags: auth, ui")
                .contains("<h2>test: domain</h2>");

    }

    @Test
    void toDocumentWrongInput() {
        // given
        Mockito.when(frontendMapping.getKeys()).thenReturn(Map.of());
        map.put("test/nivio/nivio", conditionKpi);
        URI uri = URI.create("https://www.nivio.com/");
        String[] tags = Arrays.array("auth", "ui");
        var landscape = LandscapeFactory.createForTesting("test", "test").build();
        Item foo = ItemBuilder.anItem().withLandscape(landscape).withIdentifier("nivio").withGroup("nivio").withAddress(uri).build();
        foo.setTags(tags);
        landscape.setItems(Set.of(foo));
        var assessment = AssessmentFactory.createAssessment(landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}, "reportType", new String[]{""}));

        //  when
        String document = groupingReportGenerator.toDocument(landscape, assessment, searchConfig, frontendMapping);

        // then
        assertThat(document).contains("Date: ")
                .contains(searchConfig.getTitle())
                .doesNotContain("Address: https://www.nivio.com/")
                .doesNotContain("Tags: auth, ui")
                .doesNotContain("<h2>Owners: common</h2>");

    }

    @Test
    void toDocumentNoSearchConfig() {
        // given
        Mockito.when(frontendMapping.getKeys()).thenReturn(Map.of());
        map.put("test/nivio/nivio", conditionKpi);
        URI uri = URI.create("https://www.nivio.com/");
        String[] tags = Arrays.array("auth", "ui");
        var landscape = LandscapeFactory.createForTesting("test", "test").build();
        Item foo = ItemBuilder.anItem().withLandscape(landscape).withIdentifier("nivio").withGroup("nivio").withAddress(uri).build();
        foo.setTags(tags);
        landscape.setItems(Set.of(foo));
        var assessment = AssessmentFactory.createAssessment(landscape, map);

        //  when
        String document = groupingReportGenerator.toDocument(landscape, assessment, null, frontendMapping);

        // then
        assertThat(document).contains("Date: ")
                .contains("Report")
                .doesNotContain("Address: https://www.nivio.com/")
                .doesNotContain("Tags: auth, ui")
                .doesNotContain("<h2>Owners: domain</h2>");

    }
}