package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.model.LandscapeImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for custom and builtin KPIs.
 */
@Service
public class KPIFactory {

    private final Map<String, Supplier<KPI>> defaultKPIs = new HashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public KPIFactory(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;

        //replace this once https://github.com/dedica-team/nivio/issues/14 is implemented
        defaultKPIs.put(HealthKPI.IDENTIFIER, HealthKPI::new);
        defaultKPIs.put(ScalingKPI.IDENTIFIER, ScalingKPI::new);
        defaultKPIs.put(ConditionKPI.IDENTIFIER, ConditionKPI::new);
        defaultKPIs.put(LifecycleKPI.IDENTIFIER, LifecycleKPI::new);
    }

    /**
     * Returns the default KPIs plus custom ones and configuration.
     *
     * @param landscape the landscape
     * @return effective KPIs
     */
    public Map<String, KPI> getConfiguredKPIs(LandscapeImpl landscape) {

        Map<String, KPIConfig> config = landscape.getConfig().getKPIs();
        Map<String, KPI> kpis = new HashMap<>(defaultKPIs.size() + config.size());
        defaultKPIs.forEach((s, kpiSupplier) -> kpis.put(s, kpiSupplier.get()));

        config.forEach((s, kpiConfig) -> {
            KPI kpi = kpis.get(s);
            if (kpi == null) {
                kpi = new CustomKPI();
                kpis.put(s, kpi);
            }
        });

        //init all
        kpis.forEach((s, kpi) -> {
            try {
                kpi.init(config.get(s));
            } catch (Exception e) {
                ProcessingException p;
                if (e instanceof ProcessingException) {
                    p = (ProcessingException) e;
                } else {
                    p = new ProcessingException("Failed to initialize KPI " + s, e);
                }
                landscape.getLog().error(p);
                eventPublisher.publishEvent(new ProcessingErrorEvent(this, p));
                throw p;
            }
        });

        return kpis;
    }
}
