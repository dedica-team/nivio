package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.landscape.LandscapeInterface;


public class ReadingException extends ProcessingException {
    public ReadingException(String s, Throwable e) {
        super(s,e);
    }

    public ReadingException(LandscapeInterface landscape, String s, Throwable e) {
        super(landscape, s,e);
    }
}
