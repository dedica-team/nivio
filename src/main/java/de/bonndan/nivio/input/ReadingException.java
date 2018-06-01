package de.bonndan.nivio.input;

import java.io.IOException;

public class ReadingException extends RuntimeException {
    public ReadingException(String s, IOException e) {
        super(s,e);
    }
}
