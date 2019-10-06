package de.bonndan.nivio.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.api.LandscapeDTOFactory;
import de.bonndan.nivio.api.dto.LandscapeDTO;
import de.bonndan.nivio.model.Landscape;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

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

    @JsonIgnore
    public Landscape getLandscape() {
        return landscape;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("landscape")
    public Landscape getLandscapeDTO() {
        if (landscape == null)
            return null;

        return LandscapeDTOFactory.from(landscape);
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

    private class Entry {
        private final String level;
        private final String message;

        public Entry(String level, String message) {
            this.level = level;
            this.message = message;
        }

        @Override
        @JsonValue
        public String toString() {
            return level + ": " + message;
        }
    }
}
