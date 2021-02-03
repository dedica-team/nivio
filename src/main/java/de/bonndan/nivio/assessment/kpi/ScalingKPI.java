package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.RelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * This KPI evaluates the scale label and tries to find bottlenecks where providers for many items are down or not scaled.
 *
 * red if 0 as provider for other items
 * yellow if scaled to 0 without relations
 * orange of scaled to 0 as data sink
 * unknown if no label or not a number
 * green if scaled higher than 1
 * yellow if a bottleneck (more than 1 item depend on it)
 */
public class ScalingKPI extends AbstractKPI {

    public static final String IDENTIFIER = "scaling";

    private static final Logger LOGGER = LoggerFactory.getLogger(ScalingKPI.class);
    public static final String SCALED_TO_ZERO = "scaled to zero";

    @NonNull
    @Override
    public List<StatusValue> getStatusValues(Component component) {

        if (!(component instanceof Item)) {
            return Collections.emptyList();
        }

        return getItemStatusValues((Item) component);
    }

    private List<StatusValue> getItemStatusValues(Item component) {

        long usedAsDataTarget = component.getRelations(RelationType.DATAFLOW).stream()
                .filter(relation -> relation.getTarget().equals(component))
                .count();

        long usedAsProvider = component.getRelations(RelationType.PROVIDER).stream()
                .filter(relation -> relation.getSource().equals(component))
                .count();

        String scaleLabel = component.getLabel(Label.scale);
        int scaleValue = -1;
        if (scaleLabel != null) {
            try {
                scaleValue = Integer.parseInt(scaleLabel);
                if (scaleValue == 0) {
                    Status status = Status.YELLOW;
                    String message = SCALED_TO_ZERO;
                    if (usedAsProvider > 0) {
                        status =  Status.RED;
                        message += " and provider for " + usedAsProvider + " items";
                    } else if (usedAsDataTarget > 0) {
                        status = Status.ORANGE;
                        message += " and data sink for " + usedAsDataTarget + " items";
                    }
                    return List.of(new StatusValue(Label.scale.name(), status, message));
                }
            } catch (NumberFormatException ignored) {
                LOGGER.warn("Scaling KPI cannot handle label scale value '{}' of component '{}'", scaleLabel, component);
            }
        }

        if (scaleValue == 1 && (usedAsProvider > 1)) {
            return List.of(new StatusValue(Label.scale.name(), Status.YELLOW, String.format("Unscaled, but %d items depend on it.", usedAsProvider)));
        }

        if (scaleValue > 0) {
            return List.of(new StatusValue(Label.scale.name(), Status.GREEN));
        }

        return Collections.emptyList();
    }

    @Override
    protected List<StatusValue> getStatusValues(String value, String message) {
        //unused
        return Collections.emptyList();
    }

}
