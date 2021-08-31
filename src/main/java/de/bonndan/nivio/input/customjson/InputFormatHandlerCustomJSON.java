package de.bonndan.nivio.input.customjson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.observation.InputFormatObserver;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InputFormatHandlerCustomJSON implements InputFormatHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputFormatHandlerCustomJSON.class);

    public static final String FORMAT = "customJSON";

    public static final String ITEMS_PATH_KEY = "items";
    public static final String ITEM_PATH_KEY = "item";
    public static final String FETCH = "fetch";

    private final FileFetcher fileFetcher;
    private final ObjectMapper objectMapper;

    public InputFormatHandlerCustomJSON(FileFetcher fileFetcher, ObjectMapper objectMapper) {
        this.fileFetcher = fileFetcher;
        this.objectMapper = objectMapper;

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

    @Override
    public List<String> getFormats() {
        return Collections.singletonList(FORMAT);
    }

    @Override
    public void applyData(@NonNull final SourceReference reference, @Nullable final URL baseUrl, @NonNull final LandscapeDescription landscapeDescription) {
        String itemsPath = getPath(reference, ITEMS_PATH_KEY);
        String dataSource = fileFetcher.get(reference, baseUrl);

        //assemble new absolute url
        URL relativeBaseURL = getNewBaseUrl(baseUrl, reference.getUrl());

        ArrayNode items = JsonPath.parse(dataSource).read(itemsPath);
        items.forEach(jsonNode -> {
            Map<String, Object> itemMap = new HashMap<>();

            //handle defined paths
            Map<String, List<Function<String, String>>> paths = getPaths(reference, ITEM_PATH_KEY, relativeBaseURL);
            paths.forEach((s, functions) -> {
                var text = jsonNode.toString();
                for (Function<String, String> function : functions) {
                    text = function.apply(text);
                }

                itemMap.put(s, text);
            });

            //copy all non-containers as text
            jsonNode.fields().forEachRemaining(stringJsonNodeEntry -> {
                var node = stringJsonNodeEntry.getValue();
                if (!node.isContainerNode()) {
                    itemMap.put(stringJsonNodeEntry.getKey(), node.textValue());
                }
            });
            ItemDescription itemDescription = objectMapper.convertValue(itemMap, ItemDescription.class);
            landscapeDescription.getItemDescriptions().add(itemDescription);
        });
    }

    @Nullable
    private URL getNewBaseUrl(URL baseUrl, String url) {
        String combined = URLHelper.combine(baseUrl, url);
        try {
            URL relativeBaseURL = new URL(combined);
            URI uri = relativeBaseURL.toURI();
            URI parent = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
            return parent.toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.warn(String.format("Failed to combine url %s with base url %s", url, baseUrl));
        }
        return null;
    }

    @Override
    public InputFormatObserver getObserver(InputFormatObserver inner, SourceReference sourceReference) {
        return null;
    }

    private String getPath(SourceReference reference, String key) {
        LinkedHashMap<String, Object> mapping = (LinkedHashMap<String, Object>) reference.getProperty("mapping");
        return (String) mapping.get(key);
    }

    private Map<String, List<Function<String, String>>> getPaths(SourceReference reference, String key, URL baseUrl) {
        LinkedHashMap<String, Object> mapping = (LinkedHashMap<String, Object>) reference.getProperty("mapping");
        Map<String, String> stringStringMap = (Map<String, String>) mapping.get(key);
        Map<String, List<Function<String, String>>> paths = new LinkedHashMap<>();
        stringStringMap.forEach((key1, value) -> paths.put(key1, asFunctions(value, baseUrl)));
        return paths;
    }

    /**
     * Returns a list of functions pipe the input.
     *
     * @param value   mapping entry
     * @param baseUrl optional base url
     */
    private List<Function<String, String>> asFunctions(final String value, final URL baseUrl) {
        if (value == null || value.isEmpty())
            return new ArrayList<>();

        return Arrays.stream(value.split("\\|")).map(path -> {
            if (path.trim().equalsIgnoreCase(FETCH)) {
                return (Function<String, String>) s -> fileFetcher.get(s, baseUrl);
            }

            return (Function<String, String>) s -> {
                if (s == null) return null;
                JsonNode parsed = JsonPath.parse(s).read(path.trim());
                if (parsed instanceof ArrayNode) {
                    List<String> values = new ArrayList<>();
                    parsed.elements().forEachRemaining(stringJsonNodeEntry -> values.add(stringJsonNodeEntry.textValue()));
                    return String.join(",", values);
                }
                return parsed.textValue();
            };

        }).collect(Collectors.toList());
    }
}
