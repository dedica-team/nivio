package de.bonndan.nivio.input;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Relation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Schema(description = "Changelog for a single landscape processing run (update of landscape from different sources).")
public class ProcessingChangelog {

    @Schema(description = "The key is the FullyQualifiedIdentifier of a component")
    final Map<String, Entry> changes = new HashMap<>();

    /**
     * Add a change for a component.
     *
     * @param component  the affected component
     * @param changeType created, updated, deleted
     * @param message    an optional message
     */
    public void addEntry(
            @NonNull final Component component,
            @NonNull final ChangeType changeType,
            @Nullable final String message
    ) {
        String id = Objects.requireNonNull(component).getFullyQualifiedIdentifier().toString();
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("Could not create a changelog entry id for " + component);
        }
        Entry entry = new Entry(
                component.getClass().getSimpleName(),
                Objects.requireNonNull(changeType),
                message
        );
        changes.put(id, entry);
    }

    /**
     * Add a change for a component.
     *
     * @param component  the affected component
     * @param changeType created, updated, deleted
     */
    public void addEntry(
            @NonNull final Component component,
            @NonNull final ChangeType changeType
    ) {
        addEntry(component, changeType, null);
    }

    public void addEntry(
            @NonNull final Relation relation,
            @NonNull final ChangeType changeType,
            @Nullable final String message
    ) {
        Objects.requireNonNull(relation);
        final String id = getRelationKey(relation);
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("Could not create a changelog entry id for " + relation);
        }
        Entry entry = new Entry(
                relation.getClass().getSimpleName(),
                Objects.requireNonNull(changeType),
                message
        );
        changes.put(id, entry);
    }

    String getRelationKey(final Relation relation) {
        return String.format("%s;%s",
                relation.getSource().getFullyQualifiedIdentifier().jsonValue(),
                relation.getTarget().getFullyQualifiedIdentifier().jsonValue()
        );
    }

    /**
     * Merge the given changelog entries into the current.
     *
     * @param changelog new processor changelog
     */
    public void merge(@Nullable ProcessingChangelog changelog) {
        if (changelog == null) {
            return;
        }
        changelog.changes.forEach((s, value) -> changes.merge(
                s,
                value,
                (entry1, entry2) -> new Entry(
                        entry1.componentType,
                        ChangeType.valueOf(entry1.changeType),
                        entry1.message + "; " + entry2.message
                )
        ));
    }

    public static class Entry {
        private final String componentType;
        private final String changeType;
        private final String message;

        Entry(@NonNull final String componentType,
              @NonNull final ChangeType changeType,
              @Nullable final String message
        ) {
            this.componentType = componentType;
            this.changeType = changeType.name();
            this.message = message;
        }

        @Schema(description = "The component type", allowableValues = {"Group", "Item", "Relation"})
        public String getComponentType() {
            return componentType;
        }

        @Schema(description = "The change type", allowableValues = {"CREATED", "UPDATED", "DELETED"})
        public String getChangeType() {
            return changeType;
        }

        @Nullable
        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "componentType='" + componentType + '\'' +
                    ", changeType='" + changeType + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    enum ChangeType {
        CREATED,
        UPDATED,
        DELETED
    }
}
