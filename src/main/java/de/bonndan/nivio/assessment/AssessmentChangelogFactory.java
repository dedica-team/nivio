package de.bonndan.nivio.assessment;

import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.model.Landscape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AssessmentChangelogFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssessmentChangelogFactory.class);
    private static final String COULD_NOT_FIND_COMPONENT = "Could not find component by assessment key {}";

    private AssessmentChangelogFactory() {
    }

    /**
     * Creates a simple changelog in which all entries have been created.
     */
    public static ProcessingChangelog getChanges(@NonNull final Landscape landscape,
                                                 @NonNull final Assessment update
    ) {
        ProcessingChangelog changelog = new ProcessingChangelog();
        Objects.requireNonNull(update).getResults().forEach((key, value) -> landscape.getReadAccess().get(key)
                .ifPresentOrElse(
                        component1 -> changelog.addEntry(component1, ProcessingChangelog.ChangeType.CREATED),
                        () -> LOGGER.error(COULD_NOT_FIND_COMPONENT, key)
                )
        );

        return changelog;
    }

    public static ProcessingChangelog getChanges(@NonNull final Landscape landscape,
                                                 @NonNull final Assessment current,
                                                 @NonNull final Assessment update
    ) {

        ProcessingChangelog changelog = new ProcessingChangelog();
        var readAccess = landscape.getReadAccess();

        //updated and deleted
        current.getResults().keySet().forEach(key -> readAccess.get(key).ifPresent(component1 -> {
            if (update.getResults().containsKey(key)) {
                List<String> compare = compare(current.getResults().get(key), update.getResults().get(key));
                changelog.addEntry(component1, ProcessingChangelog.ChangeType.UPDATED, compare);
            } else {
                changelog.addEntry(component1, ProcessingChangelog.ChangeType.DELETED);
            }
        }));

        //created
        update.getResults().keySet().forEach(key -> {
            if (!current.getResults().containsKey(key)) {
                readAccess.get(key).ifPresentOrElse(
                        component1 -> changelog.addEntry(component1, ProcessingChangelog.ChangeType.CREATED),
                        () -> LOGGER.error(COULD_NOT_FIND_COMPONENT, key));
            }
        });

        return changelog;
    }

    private static List<String> compare(List<StatusValue> current, List<StatusValue> update) {
        var diff = new ArrayList<String>();
        var currentMap = new HashMap<String, StatusValue>();
        current.forEach(statusValue -> currentMap.put(statusValue.getField(), statusValue));
        var updateMap = new HashMap<String, StatusValue>();
        update.forEach(statusValue -> updateMap.put(statusValue.getField(), statusValue));

        currentMap.forEach((field, statusValue) -> {
            if (!updateMap.containsKey(field)) {
                diff.add(String.format("%s status has been removed", field));
                return;
            }

            StatusValue updateStatus = updateMap.get(field);
            if (statusValue.getStatus() != updateStatus.getStatus()) {
                diff.add(String.format("%s status has changed to %s", updateStatus.getField(), updateStatus.getStatus()));
            }
        });

        updateMap.forEach((s, statusValue) -> {
            if (!currentMap.containsKey(s))
                diff.add(String.format("%s status has been added", s));
        });

        return diff;
    }
}
