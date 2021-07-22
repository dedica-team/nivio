package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.input.ProcessingException;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Factory for custom and builtin KPIs.
 *
 *
 */
public class KPIFactory {

    private final Map<String, Supplier<KPI>> defaultKPIs = new HashMap<>();

    public KPIFactory() {

        //replace this once https://github.com/dedica-team/nivio/issues/14 is implemented
        defaultKPIs.put(HealthKPI.IDENTIFIER, HealthKPI::new);
        defaultKPIs.put(ScalingKPI.IDENTIFIER, ScalingKPI::new);
        defaultKPIs.put(ConditionKPI.IDENTIFIER, ConditionKPI::new);
        defaultKPIs.put(LifecycleKPI.IDENTIFIER, LifecycleKPI::new);
        defaultKPIs.put(KubernetesKPI.IDENTIFIER, KubernetesKPI::new);
    }

    /**
     * Returns the default KPIs plus custom ones and configuration.
     *
     * @param kpiConfigMap the landscape kpi configurations
     * @return effective KPIs
     * @throws ProcessingException if a KPI is misconfigured
     */
    public Map<String, KPI> getConfiguredKPIs(@NonNull Map<String, KPIConfig> kpiConfigMap) {

        Objects.requireNonNull(kpiConfigMap, "kpi config is null");

        Map<String, KPI> kpis = new HashMap<>(defaultKPIs.size() + kpiConfigMap.size());
        defaultKPIs.forEach((s, kpiSupplier) -> kpis.put(s, kpiSupplier.get()));

        kpiConfigMap.forEach((s, kpiConfig) -> {
            KPI kpi = kpis.get(s);
            if (kpi == null) {
                kpi = new CustomKPI();
                kpis.put(s, kpi);
            }
        });

        //init all
        kpis.forEach((s, kpi) -> {
            try {
                kpi.init(kpiConfigMap.get(s));
            } catch (Exception e) {
                ProcessingException p;
                if (e instanceof ProcessingException) {
                    p = (ProcessingException) e;
                } else {
                    p = new ProcessingException("Failed to initialize KPI " + s, e);
                }
                throw p;
            }
        });

        return kpis;
    }
}
