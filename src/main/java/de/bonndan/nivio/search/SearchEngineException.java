package de.bonndan.nivio.search;

public class SearchEngineException extends RuntimeException {
    public SearchEngineException(String message, Throwable e) {
        super(message, e);
    }

    public SearchEngineException(String message) {
        super(message);
    }
}
