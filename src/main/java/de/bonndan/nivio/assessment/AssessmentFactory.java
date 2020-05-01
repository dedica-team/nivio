package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.LandscapeImpl;

public class AssessmentFactory {

    /**
     * Assesses a landscape component (which implements {@link Assessable}) by applying all known KPIs to its fields and
     * mapping the derived {@link StatusValue}s to the {@link de.bonndan.nivio.model.FullyQualifiedIdentifier}.
     *
     * @return the assessment result
     */
    public Assessment assess(LandscapeImpl landscape) {
        return new Assessment(landscape.applyKPIs(landscape.getConfig().getKPIs()));
    }
}
