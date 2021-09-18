package de.bonndan.nivio.input.customjson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import de.bonndan.nivio.input.FileFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.*;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionFactory.class);

    public static final String FETCH = "fetch";
    public static final String FIND = "find";
    public static final char QUOTATION_CHAR = '"';

    private final FileFetcher fileFetcher;

    public FunctionFactory(FileFetcher fileFetcher) {
        this.fileFetcher = fileFetcher;

        //configures json path
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonNodeJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
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

        String[] steps = parseLine(pipedSteps);
        return Arrays.stream(steps)
                .map(String::trim)
                .map(step -> {
                    if (step.equalsIgnoreCase(FETCH)) {
                        return getFileFetcherFunction(baseUrl);
                    }

                    if (step.toLowerCase(Locale.ROOT).startsWith(FIND)) {
                        return getFindFunction(step);
                    }

                    return getJsonPathFunction(step);
                })
                .collect(Collectors.toList());
    }

    private String[] parseLine(String pipedSteps) {
        if (pipedSteps.length() > 1000) {
            throw new IllegalArgumentException(String.format("The given steps '%s...' length exceeds 1000 chars.", pipedSteps.substring(0, 20)));
        }
        return pipedSteps.split("\\|(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 16);
    }

    private Function<String, String> getFileFetcherFunction(URL baseUrl) {
        return s -> {
            LOGGER.info("fetching {} with base url {}", s, baseUrl);
            return fileFetcher.get(s, baseUrl);
        };
    }

    private Function<String, String> getJsonPathFunction(String step) {
        JsonPath.compile(step.trim());
        return s -> {
            if (s == null) return "";
            Object parsed;
            try {
                parsed = JsonPath.parse(s).read(step.trim());
            } catch (PathNotFoundException e) {
                return "";
            }

            if (parsed instanceof JsonNode) {
                if (parsed instanceof ArrayNode) {
                    List<String> values = new ArrayList<>();
                    ((ArrayNode) parsed).elements().forEachRemaining(stringJsonNodeEntry -> values.add(stringJsonNodeEntry.textValue()));
                    return String.join(COLLECTION_DELIMITER, values);
                }

                if (parsed instanceof NumericNode) {
                    return ((NumericNode) parsed).numberValue().toString();
                }
                return ((JsonNode) parsed).textValue();
            }
            //turns out that can be Integer etc., too.
            return String.valueOf(parsed);
        };
    }

    /**
     * based on https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
     *
     * @throws PatternSyntaxException on wrong regexes
     */
    private Function<String, String> getFindFunction(final String path) {

        String regex = path.replaceFirst(FIND, "").trim();
        regex = StringUtils.trimLeadingCharacter(regex, QUOTATION_CHAR);
        regex = StringUtils.trimTrailingCharacter(regex, QUOTATION_CHAR);
        // we do not trim a possible trailing
        Pattern pattern = Pattern.compile(regex);
        return s -> {
            if (!StringUtils.hasLength(s)) return "";
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return "";
        };
    }
}
