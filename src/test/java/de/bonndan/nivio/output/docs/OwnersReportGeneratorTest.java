package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.assessment.AssessmentFactory;
import de.bonndan.nivio.assessment.kpi.ConditionKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.IconService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OwnersReportGeneratorTest {

    @Test
    void toDocument() {
        var ownersReportGenerator = new OwnersReportGenerator(Mockito.mock(LocalServer.class), Mockito.mock(IconService.class));
        var foo = ItemFactory.getTestItem("a", "test");
        var bar = ItemFactory.getTestItem("b", "test");
        var conditionKpi = new ConditionKPI();
        var map = new HashMap<String, KPI>();
        map.put("test", conditionKpi);
        var landscape = LandscapeFactory.createForTesting("test", "test").withItems(Set.of(foo, bar)).build();
        var assessment = AssessmentFactory.createAssessment(landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}));
        assertThat(ownersReportGenerator.toDocument(landscape, assessment, searchConfig)).contains("Date: " + ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)));
        assertThat(ownersReportGenerator.toDocument(landscape, assessment, searchConfig)).contains(searchConfig.getTitle());
    }
}