package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.model.Landscape;


public class ReadingException extends ProcessingException {
    public ReadingException(String s, Throwable e) {
        super(s,e);
    }

    public ReadingException(Landscape landscape, String s, Throwable e) {
        super(landscape, s,e);
    }
}
