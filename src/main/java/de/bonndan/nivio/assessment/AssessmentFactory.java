package de.bonndan.nivio.assessment;

import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.model.LandscapeImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AssessmentFactory {

    private final ApplicationEventPublisher eventPublisher;

    public AssessmentFactory(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Assesses a landscape component (which implements {@link Assessable}) by applying all known KPIs to its fields and
     * mapping the derived {@link StatusValue}s to the {@link de.bonndan.nivio.model.FullyQualifiedIdentifier}.
     *
     * @return the assessment result
     */
    public Assessment assess(LandscapeImpl landscape) {
        landscape.getConfig().getKPIs().values().forEach(kpi -> {
            try {
                kpi.init();
            } catch (Exception e) {
                ProcessingException p;
                if (e instanceof ProcessingException) {
                    p = (ProcessingException) e;
                } else {
                    p = new ProcessingException("Failed to initialize KPI", e);
                }
                landscape.getLog().error("Failed to initialize KPI", p);
                eventPublisher.publishEvent(new ProcessingErrorEvent(this, p));
            }
        });
        return new Assessment(landscape.applyKPIs(landscape.getConfig().getKPIs()));
    }
}
