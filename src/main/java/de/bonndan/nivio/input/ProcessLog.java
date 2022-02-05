package de.bonndan.nivio.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is a decorator for {@link Logger} used during landscape processing in order to grab all landscape relevant
 * processing events.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessLog {

    private final Logger logger;

    private final List<Entry> messages = new ArrayList<>();

    private final String landscapeIdentifier;
    private ProcessingChangelog changelog;

    public ProcessLog(@NonNull final Logger logger, @NonNull final String landscapeIdentifier) {
        this.logger = Objects.requireNonNull(logger);
        this.landscapeIdentifier = Objects.requireNonNull(landscapeIdentifier);
    }

    public void debug(String message) {
        messages.add(new Entry("DEBUG", message));
        logger.debug(message);
    }

    public void info(String message) {
        messages.add(new Entry("INFO", message));
        logger.info(message);
    }

    public void warn(String msg, ProcessingException e) {
        messages.add(new Entry("WARN", msg));
        logger.warn(msg, e);
    }

    public void warn(String msg) {
        logger.warn(msg);
        messages.add(new Entry("WARN", msg));
    }

    public void error(String msg) {
        logger.error(msg);
        messages.add(new Entry("ERROR", msg));
    }

    public void error(ProcessingException e) {
        messages.add(new Entry("ERROR", e));
        logger.error(e.getMessage(), e);
    }

    @JsonProperty("landscape")
    public String getLandscapeIdentifier() {
        return landscapeIdentifier;
    }

    public List<Entry> getMessages() {
        return messages;
    }

    @JsonIgnore
    public ZonedDateTime getLastUpdate() {

        if (!messages.isEmpty()) {
            return messages.get(messages.size() - 1).date;
        }
        return null;
    }

    public void setChangelog(ProcessingChangelog changelog) {
        this.changelog = Objects.requireNonNull(changelog);
    }

    public ProcessingChangelog getChangelog() {
        return changelog;
    }

    public static class Entry {

        public final String level;

        public final String message;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        public final ZonedDateTime date;

        public Entry(String level, String message) {
            this.level = level;
            this.message = message;
            this.date = ZonedDateTime.now();
        }

        public Entry(String level, ProcessingException e) {
            this.level = level;
            this.message = e.getMessage();
            this.date = ZonedDateTime.now();
        }
    }
}
