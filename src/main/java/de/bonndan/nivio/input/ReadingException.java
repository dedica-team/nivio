package de.bonndan.nivio.input;

import java.io.IOException;

public class ReadingException extends RuntimeException {
    public ReadingException(String s, Throwable e) {
        super(s,e);
    }
}
