package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static de.bonndan.nivio.assessment.StatusValue.SUMMARY_LABEL;

/**
 * Interface for components that can be assessed and can have assigned {@link StatusValue}s.
 */
public interface Assessable extends Component {

    /**
     * Returns the highest status as summary of all statusValues.
     */
    default StatusValue getSummary() {

        final AtomicReference<StatusValue> summary = new AtomicReference<>();
        summary.set(new StatusValue(SUMMARY_LABEL, Status.UNKNOWN));

        getStatusValues().forEach(statusItem -> {
            if (statusItem == null) {
                return;
            }

            if (statusItem.getStatus().isHigherThan(summary.get().getStatus())) {
                summary.set(statusItem);
            }
        });

        return new StatusValue(SUMMARY_LABEL, summary.get().getStatus());
    }

    Set<StatusValue> getStatusValues();

    default void setStatusValue(StatusValue statusValue) {

        if (statusValue == null) {
            throw new IllegalArgumentException("Status value is null");
        }
        if (StringUtils.isEmpty(statusValue.getField())) {
            throw new IllegalArgumentException("Status value has no field");
        }

        getStatusValues().add(statusValue);
    }

    default List<? extends Assessable> getChildren() {
        return new ArrayList<>();
    }
}
