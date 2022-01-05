package de.bonndan.nivio.input;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Relation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Schema(description = "Changelog for a single landscape processing run (update of landscape from different sources).")
public class ProcessingChangelog {

    @Schema(description = "The key is the FullyQualifiedIdentifier of a component")
    private final Map<String, Entry> changes = new HashMap<>();

    /**
     * Add a change for a component.
     *
     * @param component  the affected component
     * @param changeType created, updated, deleted
     * @param messages   an optional message
     */
    public void addEntry(
            @NonNull final Component component,
            @NonNull final ChangeType changeType,
            @Nullable final List<String> messages
    ) {
        String id = Objects.requireNonNull(component).getFullyQualifiedIdentifier().toString();
        if (!StringUtils.hasLength(id)) {
            throw new IllegalArgumentException("Could not create a changelog entry id for " + component);
        }
        Entry entry = new Entry(
                component.getClass().getSimpleName(),
                Objects.requireNonNull(changeType),
                messages
        );
        getChanges().put(id, entry);
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

    /**
     * Adds a relation change
     *
     * @param relation   the relation that has changed.
     * @param changeType created, updated ...
     * @param messages   the messages
     */
    public void addEntry(
            @NonNull final Relation relation,
            @NonNull final ChangeType changeType,
            @Nullable final List<String> messages
    ) {
        Objects.requireNonNull(relation);

        Entry entry = new Entry(
                Relation.class.getSimpleName(),
                Objects.requireNonNull(changeType),
                messages
        );
        getChanges().put(relation.getIdentifier(), entry);
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
        changelog.getChanges().forEach((s, value) -> getChanges().merge(
                s,
                value,
                (entry1, entry2) -> new Entry(
                        entry1.componentType,
                        ChangeType.valueOf(entry1.changeType),
                        Stream.concat(entry1.getMessages().stream(), entry2.getMessages().stream())
                                .collect(Collectors.toList())
                )
        ));
    }

    public Map<String, Entry> getChanges() {
        return changes;
    }

    public static class Entry {
        private final String componentType;
        private final String changeType;
        private final List<String> messages;

        Entry(@NonNull final String componentType,
              @NonNull final ChangeType changeType,
              @Nullable final List<String> messages
        ) {
            this.componentType = componentType;
            this.changeType = changeType.name();
            this.messages = messages == null ? new ArrayList<>() : messages;
        }

        @Schema(description = "The component type", allowableValues = {"Group", "Item", "Relation"})
        public String getComponentType() {
            return componentType;
        }

        @Schema(description = "The change type", allowableValues = {"CREATED", "UPDATED", "DELETED"})
        public String getChangeType() {
            return changeType;
        }

        @NonNull
        public List<String> getMessages() {
            return messages;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "componentType='" + componentType + '\'' +
                    ", changeType='" + changeType + '\'' +
                    ", messages='" + messages + '\'' +
                    '}';
        }
    }

    public enum ChangeType {
        CREATED,
        UPDATED,
        DELETED
    }
}
