package de.bonndan.nivio.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.model.Landscape;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a decorator for {@link Logger} used during landscape processing in order to grab all landscape relevant
 * processing events.
 */
public class ProcessLog {

    private final Logger logger;

    private final List<Entry> messages = new ArrayList<>();

    @JsonIgnore
    private Landscape landscape;

    @JsonIgnore
    private ProcessingException exception;

    public ProcessLog(Logger logger) {
        this.logger = logger;
    }

    public ProcessLog(ProcessingException e) {
        this.logger = null;
        exception = e;
        landscape = e.getLandscape();
    }

    public void debug(String message) {
        messages.add(new Entry("DEBUG", message));
        logger.debug(message);
    }

    public void info(String message) {
        messages.add(new Entry("INFO", message));
        logger.info(message);
    }

    public void setLandscape(Landscape landscape) {
        this.landscape = landscape;
    }

    public void warn(String msg, ProcessingException e) {
        messages.add(new Entry("WARN", msg));
        logger.warn(msg, e);
        this.exception = e;
    }

    public void warn(String msg) {
        logger.warn(msg);
        messages.add(new Entry("WARN", msg));
    }

    public void error(String msg, ProcessingException e) {
        messages.add(new Entry("ERROR", msg));
        logger.error(msg, e);
        this.exception = e;
    }

    @JsonIgnore
    public Landscape getLandscape() {
        return landscape;
    }

    public List<Entry> getMessages() {
        return messages;
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getError() {
        if (exception == null)
            return null;

        return exception.getMessage();
    }

    @JsonIgnore
    public LocalDateTime getLastUpdate() {

        if (messages.size() > 0) {
            return messages.get(messages.size() - 1).date;
        }
        return null;
    }

    public static class Entry {

        public final String level;

        public final String message;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        public final LocalDateTime date;

        public Entry(String level, String message) {
            this.level = level;
            this.message = message;
            this.date = LocalDateTime.now();
        }
    }
}
