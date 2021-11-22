package de.bonndan.nivio.util;

public class IconCannotBeLoadedException extends RuntimeException {

    public IconCannotBeLoadedException(String errorMessage) {
        super(errorMessage);
    }
}