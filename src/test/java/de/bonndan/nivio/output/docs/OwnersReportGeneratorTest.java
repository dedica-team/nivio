package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.assessment.AssessmentFactory;
import de.bonndan.nivio.assessment.kpi.ConditionKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.IconService;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OwnersReportGeneratorTest {

    @Test
    void toDocument() {
        var ownersReportGenerator = new OwnersReportGenerator(Mockito.mock(LocalServer.class), Mockito.mock(IconService.class));
        var conditionKpi = new ConditionKPI();
        var map = new HashMap<String, KPI>();
        map.put("test", conditionKpi);
        URI uri = URI.create("https://www.nivio.com/");
        String[] tags = Arrays.array("auth", "ui");
        var landscape = LandscapeFactory.createForTesting("test", "test").build();
        Item foo = new Item("nivio", landscape, "nivio", null, null, null,
                null, null, null, null, uri);
        landscape.setItems(Set.of(foo));
        var assessment = AssessmentFactory.createAssessment(landscape, map);
        var searchConfig = new SearchConfig(Map.of("title", new String[]{"test"}));

        assertThat(ownersReportGenerator.toDocument(landscape, assessment, searchConfig)).contains("Date: " + ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)));
        assertThat(ownersReportGenerator.toDocument(landscape, assessment, searchConfig)).contains(searchConfig.getTitle());
        assertTrue(ownersReportGenerator.toDocument(landscape, assessment, searchConfig).contains("Address: https://www.nivio.com/"));
//        assertTrue(ownersReportGenerator.toDocument(landscape, assessment, searchConfig).contains("Tags: auth, ui"));
    }
}