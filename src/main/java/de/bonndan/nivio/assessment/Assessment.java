package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.KPI;

import java.util.List;
import java.util.Map;

public class Assessment {

    private final Map<String, KPI> kpis;

    public Assessment(Map<String, KPI> kpis) {
        this.kpis = kpis;
    }

    /**
     * Depth first assessment.
     *
     * @param assessable a landscape component be be assessed.
     */
    public void assess(Assessable assessable) {
        assessable.getChildren().forEach(this::assess);
        kpis.forEach((id, kpi) -> kpi.getStatusValues(assessable).forEach(assessable::setStatusValue));
    }
}
