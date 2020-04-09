package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.KPI;

import java.util.List;

public class Assessment {

    private final List<KPI> kpis;

    public Assessment(List<KPI> kpis) {
        this.kpis = kpis;
    }

    /**
     * Breadth first assessment.
     *
     * @param assessable a landscape component be be assessed.
     */
    public void assess(Assessable assessable) {
        assessable.getChildren().forEach(this::assess);
        kpis.forEach(kpi -> kpi.getStatusValues(assessable).forEach(assessable::setStatusValue));
    }
}
