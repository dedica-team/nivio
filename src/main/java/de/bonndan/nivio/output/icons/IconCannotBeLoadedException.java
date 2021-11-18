package de.bonndan.nivio.output.icons;

public class IconCannotBeLoadedException extends RuntimeException {

    public IconCannotBeLoadedException(String errorMessage) {
        super(errorMessage);
    }
}