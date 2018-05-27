package de.bonndan.nivio.applayer;

import java.io.IOException;

public class ServiceReadingException extends RuntimeException {
    public ServiceReadingException(String s, IOException e) {
        super(s, e);
    }
}
