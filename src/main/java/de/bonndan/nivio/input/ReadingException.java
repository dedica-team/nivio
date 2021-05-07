package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An exception used specifically for input parsing or mapping errors.
 *
 *
 */
public class ReadingException extends ProcessingException {


    public ReadingException(String s, Throwable e) {
        super(s, e);
    }

    public ReadingException(LandscapeDescription landscape, String s, Throwable e) {
        super(landscape, s, e);
    }

    /**
     * Creates a reading exception from failed json mapping.
     *
     * @param source the input file/string
     * @param e the exception
     */
    public static ReadingException from(String source, JsonMappingException e) {

        if (e instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException ex = (UnrecognizedPropertyException) e;
            String path = Arrays.stream(ex.getPathReference().split("->"))
                    .map(s -> s.substring(s.indexOf("[")).replace("[", "").replace("]", "").replace("\"", ""))
                    .collect(Collectors.joining("/"));
            return new ReadingException(source +" contains unknown field '" + ex.getPropertyName() + "' in " + path, e);
        }

        return from(source, e.getMessage(), e);
    }

    /**
     * Creates a reading exception from a string message and exception (generic).
     *
     * @param source the input file/string
     * @param message a custom message
     * @param e the exception
     */
    public static ReadingException from(@NonNull final String source, @NonNull final String message, @NonNull final Throwable e) {
        return new ReadingException(String.format("%s in %s", Objects.requireNonNull(message), source), e);
    }
}
