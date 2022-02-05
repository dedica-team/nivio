package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.input.ProcessingException;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Factory for custom and builtin KPIs.
 */
public class KPIFactory {

    private final Map<String, Supplier<KPI>> builtInKPIs = new HashMap<>();

    public KPIFactory() {

        //replace this once https://github.com/dedica-team/nivio/issues/14 is implemented
        builtInKPIs.put(HealthKPI.IDENTIFIER, HealthKPI::new);
        builtInKPIs.put(ScalingKPI.IDENTIFIER, ScalingKPI::new);
        builtInKPIs.put(ConditionKPI.IDENTIFIER, ConditionKPI::new);
        builtInKPIs.put(LifecycleKPI.IDENTIFIER, LifecycleKPI::new);
        builtInKPIs.put(KubernetesKPI.IDENTIFIER, KubernetesKPI::new);
    }

    /**
     * Returns the default KPIs plus custom ones and configuration.
     *
     * @param kpiConfigMap the landscape kpi configurations
     * @return effective KPIs
     * @throws ProcessingException if a KPI is misconfigured
     */
    public Map<String, KPI> getConfiguredKPIs(@NonNull Map<String, KPIConfig> kpiConfigMap) {

        Objects.requireNonNull(kpiConfigMap, "KPI config is null");

        Map<String, KPI> kpis = new HashMap<>( kpiConfigMap.size());

        kpiConfigMap.forEach((s, kpiConfig) -> {
            KPI kpi = kpis.get(s);
            if (kpi == null) {
                kpi = builtInKPIs.containsKey(s) ? builtInKPIs.get(s).get() : new CustomKPI();
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
                    p = new ProcessingException(String.format("Failed to initialize KPI %s", s), e);
                }
                throw p;
            }
        });

        return kpis;
    }

}
