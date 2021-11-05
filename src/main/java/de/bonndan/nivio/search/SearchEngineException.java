package de.bonndan.nivio.search;

import java.io.IOException;

public class SearchEngineException extends RuntimeException {
    public SearchEngineException(String message, IOException e) {
        super(message, e);
    }

    public SearchEngineException(String message) {
        super(message);
    }
}
