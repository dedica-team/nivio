package de.bonndan.nivio.search;


public class SearchException extends RuntimeException {
    public SearchException(String message) {
        super(message);
    }

    public SearchException(String message, Throwable e) {
        super(message, e);
    }
}
