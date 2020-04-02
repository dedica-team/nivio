package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.LandscapeItem;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A configured key performance indicator related to a landscape item label.
 *
 *
 */
public class KPI {

    private final String label;
    private final String messageLabel;

    /**
     * @param label        the label which is evaluated for status
     * @param messageLabel the label which is used as optional message
     */
    public KPI(@NonNull String label, @Nullable String messageLabel) {
        this.label = label;
        this.messageLabel = messageLabel;
    }

    /**
     * Returns the status evaluation of the item on the configured field.
     *
     * @param item item to assess
     * @return current status value, unknown if not present
     */
    @NonNull
    public StatusValue forItem(LandscapeItem item) {
        String value = item.getLabel(label);
        AtomicReference<Status> itemStatus = new AtomicReference<>();
        for (Status status : Status.values()) {
            Optional<Range> ro = getRange(status);
            if (ro.isPresent()) {
                Optional<Status> status1 = ro.get().getStatus(Integer.valueOf(value));
                if (status1.isPresent()) {
                    return new StatusValue(label, status1.get(), item.getLabel(messageLabel));
                }
            }

            getMatch(status).flatMap(match -> match.getStatus(value)).ifPresent(itemStatus::set);
        }

        return new StatusValue(label, Status.UNKNOWN, item.getLabel(messageLabel));
    }

    private Optional<Match> getMatch(Status status) {
        return Optional.empty();
    }

    private Optional<Range> getRange(Status status) {
        return Optional.empty();
    }

    public static class Range {
        public Optional<Status> getStatus(Integer value) {
            return Optional.empty();
        }
    }

    private static class Match {
        public Optional<Status> getStatus(String value) {
            return Optional.empty();
        }
    }
}
