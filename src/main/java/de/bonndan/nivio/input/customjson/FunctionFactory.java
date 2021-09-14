package de.bonndan.nivio.input.customjson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.JsonPath;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import de.bonndan.nivio.input.FileFetcher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static de.bonndan.nivio.input.LabelToFieldResolver.COLLECTION_DELIMITER;

/**
 * Creates functions for mapping steps.
 */
@Component
public class FunctionFactory {

    public static final String FETCH = "fetch";
    public static final String FIND = "find";
    public static final char QUOTATION_CHAR = '"';

    private final FileFetcher fileFetcher;
    private final CSVParser parser;

    public FunctionFactory(FileFetcher fileFetcher) {
        this.fileFetcher = fileFetcher;
        //escape char is backslash
        parser = new CSVParserBuilder().withSeparator('|').withQuoteChar(QUOTATION_CHAR).build();
    }

    /**
     * Returns a list of functions that pipe the input and throw exceptions
     *
     * @param pipedSteps mapping entry
     * @param baseUrl    optional base url
     * @throws RuntimeException if a pattern or json path cannot be parsed
     */
    public List<Function<String, String>> asFunctions(@Nullable final String pipedSteps, final URL baseUrl) {
        if (pipedSteps == null || pipedSteps.isEmpty()) return new ArrayList<>();

        String[] steps;
        try {
            steps = parser.parseLine(pipedSteps);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Could not parse steps: %s", pipedSteps));
        }

        return Arrays.stream(steps)
                .map(String::trim)
                .map(step -> {
                    if (step.equalsIgnoreCase(FETCH)) {
                        return (Function<String, String>) s -> fileFetcher.get(s, baseUrl);
                    }

                    if (step.toLowerCase(Locale.ROOT).startsWith(FIND)) {
                        return getFindFunction(step);
                    }

                    return getJsonPathFunction(step);
                })
                .collect(Collectors.toList());
    }

    private Function<String, String> getJsonPathFunction(String step) {
        JsonPath.compile(step.trim());
        return s -> {
            if (s == null) return null;

            Object parsed = JsonPath.parse(s).read(step.trim());
            if (parsed instanceof JsonNode) {
                if (parsed instanceof ArrayNode) {
                    List<String> values = new ArrayList<>();
                    ((ArrayNode) parsed).elements().forEachRemaining(stringJsonNodeEntry -> values.add(stringJsonNodeEntry.textValue()));
                    return String.join(COLLECTION_DELIMITER, values);
                }

                return ((JsonNode) parsed).textValue();
            }

            //turns out that can be Integer etc., too.
            return String.valueOf(parsed);
        };
    }

    /**
     * @throws PatternSyntaxException on wrong regexes
     */
    private Function<String, String> getFindFunction(final String path) {

        String regex = path.replaceFirst(FIND, "").trim();
        regex = StringUtils.trimLeadingCharacter(regex, QUOTATION_CHAR);
        // we do not trim a possible trailing
        Pattern pattern = Pattern.compile(regex);
        return s -> {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return "";
        };
    }
}
