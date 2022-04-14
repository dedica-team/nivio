package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.output.dto.RangeApiModel;
import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    private final Map<Status, RangeApiModel> ranges = Map.of(
            Status.GREEN, new RangeApiModel(Range.between(1D, Double.POSITIVE_INFINITY)),
            Status.YELLOW, new RangeApiModel(Range.between(0D, 0D), SCALED_TO_ZERO),
            Status.ORANGE, new RangeApiModel(Range.between(0D, 0D), "data sink scaled to zero"),
            Status.RED, new RangeApiModel(Range.between(0D, 0D), "provider scaled to zero")
    );

    @Override
    public String getDescription() {
        return "Turns yellow if the 'scale' label is zero, orange if it is a data sink, and red if it is a provider.";
    }

    @NonNull
    @Override
    public List<StatusValue> getStatusValues(Assessable component) {

        if (!(component instanceof Item)) {
            return Collections.emptyList();
        }

        return getItemStatusValues((Item) component);
    }

    private List<StatusValue> getItemStatusValues(Item component) {

        long usedAsDataTarget = RelationType.DATAFLOW.filter(component.getRelations()).stream()
                .filter(relation -> relation.getTarget().equals(component))
                .count();

        long usedAsProvider = RelationType.PROVIDER.filter(component.getRelations()).stream()
                .filter(relation -> relation.getSource().equals(component))
                .count();

        String scaleLabel = component.getLabel(Label.scale);
        int scaleValue = -1;
        URI assessmentIdentifier = component.getFullyQualifiedIdentifier();
        if (scaleLabel != null) {
            try {
                scaleValue = Integer.parseInt(scaleLabel);
                if (scaleValue == 0) {
                    Status status = Status.YELLOW;
                    String message = SCALED_TO_ZERO;
                    if (usedAsProvider > 0) {
                        status = Status.RED;
                        message += " and provider for " + usedAsProvider + " items";
                    } else if (usedAsDataTarget > 0) {
                        status = Status.ORANGE;
                        message += " and data sink for " + usedAsDataTarget + " items";
                    }
                    return List.of(new StatusValue(assessmentIdentifier, Label.scale.name(), status, message));
                }
            } catch (NumberFormatException ignored) {
                LOGGER.warn("Scaling KPI cannot handle label scale value '{}' of component '{}'", scaleLabel, component);
            }
        }

        if (scaleValue == 1 && (usedAsProvider > 1)) {
            return List.of(new StatusValue(assessmentIdentifier, Label.scale.name(), Status.YELLOW, String.format("unscaled, but %d items depend on it", usedAsProvider)));
        }

        if (scaleValue > 0) {
            return List.of(new StatusValue(assessmentIdentifier, Label.scale.name(), Status.GREEN, ""));
        }

        return Collections.emptyList();
    }

    @Override
    protected List<StatusValue> getStatusValues(@NonNull final Assessable assessable, String value, String message) {
        //unused
        return Collections.emptyList();
    }

    @Override
    @Nullable
    public Map<Status, RangeApiModel> getRanges() {
        return sorted(ranges);
    }

    @Override
    @Nullable
    public Map<Status, List<String>> getMatches() {
        return null;
    }
}
