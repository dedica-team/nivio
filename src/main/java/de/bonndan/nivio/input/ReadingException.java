package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.model.Landscape;

import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.stream.Collectors;


public class ReadingException extends ProcessingException {


    public ReadingException(String s, Throwable e) {
        super(s, e);
    }

    public ReadingException(Landscape landscape, String s, Throwable e) {
        super(landscape, s, e);
    }

    public static ReadingException from(String source, JsonMappingException e) {

        if (e instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException ex = (UnrecognizedPropertyException) e;
            String path = Arrays.stream(ex.getPathReference().split("->"))
                    .map(s -> s.substring(s.indexOf("[")).replace("[", "").replace("]", "").replace("\"", ""))
                    .collect(Collectors.joining("/"));
            return new ReadingException(source +" contains unknown field '" + ex.getPropertyName() + "' in " + path, e);
        }


        return new ReadingException(e.getMessage() + " in " + source, e);
    }

}
