package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.CustomKPI;
import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.model.LandscapeImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AssessmentFactoryTest {

    private ApplicationEventPublisher publisher;
    private AssessmentFactory factory;

    @Test
    public void testInitsKPIs() {

        publisher = mock(ApplicationEventPublisher.class);
        factory =new AssessmentFactory(publisher);

        CustomKPI kpi = mock(CustomKPI.class);
        LandscapeImpl landscape = getLandscape(List.of(kpi));

        //when
        Assessment assess = factory.assess(landscape);

        //then
        assertNotNull(assess);
        verify(kpi).init();
    }

    private LandscapeImpl getLandscape(List<AbstractKPI> kpis) {
        LandscapeImpl landscape = new LandscapeImpl();
        landscape.setIdentifier("foo");
        kpis.forEach(kpi -> {
            landscape.getConfig().getKPIs().put(kpi.getDescription(), kpi);
        });
        return landscape;
    }
}