package de.bonndan.nivio.input.customjson;

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
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.SourceReference;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.observation.InputFormatObserver;
import de.bonndan.nivio.util.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    private final FileFetcher fileFetcher;
    private final ObjectMapper objectMapper;
    private final FunctionFactory functionFactory;

    public InputFormatHandlerCustomJSON(FileFetcher fileFetcher, ObjectMapper objectMapper, FunctionFactory functionFactory) {
        this.fileFetcher = fileFetcher;
        this.objectMapper = objectMapper;
        this.functionFactory = functionFactory;

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
    public List<LandscapeDescription> applyData(@NonNull final SourceReference reference, @NonNull final LandscapeDescription landscapeDescription) {
        String itemsPath = getPath(reference, ITEMS_PATH_KEY);
        if (!StringUtils.hasLength(itemsPath)) {
            LOGGER.warn("No items path configured in mapping, cannot process custom JSON");
            return Collections.emptyList();
        }
        String dataSource = fileFetcher.get(reference);

        //assemble new absolute url
        URL currentFileBaseURL = getNewBaseUrl(reference.getSeedConfig().getBaseUrl(), reference.getUrl().toString());

        Map<String, LandscapeDescription> landscapeDescriptionMap = new HashMap<>();
        landscapeDescriptionMap.put(landscapeDescription.getIdentifier(), landscapeDescription);
        ArrayNode items = JsonPath.parse(dataSource).read(itemsPath);
        items.forEach(jsonNode -> {
            Map<String, Object> itemMap = new HashMap<>();

            //handle defined paths
            Map<String, List<Function<String, String>>> paths = getPaths(reference, ITEM_PATH_KEY, currentFileBaseURL);
            paths.forEach((s, functions) -> {
                var text = jsonNode.toString();
                try {
                    //https://stackoverflow.com/a/44521687
                    text = functions.stream().reduce(Function.identity(), Function::andThen).apply(text);
                    if (StringUtils.hasLength(text)) {
                        itemMap.put(s, text);
                    }
                } catch (Exception e) {
                    throw new ProcessingException(reference, String.format("Failed to handle mapping for field '%s': %s", s, e.getMessage()), e);
                }
            });

            //copy all non-containers as text
            jsonNode.fields().forEachRemaining(stringJsonNodeEntry -> {
                var node = stringJsonNodeEntry.getValue();
                if (!node.isContainerNode()) {
                    itemMap.put(stringJsonNodeEntry.getKey(), node.textValue());
                }
            });
            ItemDescription itemDescription = objectMapper.convertValue(itemMap, ItemDescription.class);
            if (!StringUtils.hasLength(itemDescription.getFullyQualifiedIdentifier().getLandscape())) {
                itemDescription.setEnvironment(landscapeDescription.getIdentifier());
            }
            String landscapeId = itemDescription.getFullyQualifiedIdentifier().getLandscape();
            LandscapeDescription applyToLandscape = landscapeDescriptionMap.computeIfAbsent(
                    landscapeId, LandscapeDescription::new
            );
            applyToLandscape.getItemDescriptions().add(itemDescription);
        });

        return landscapeDescriptionMap.values().stream().collect(Collectors.toUnmodifiableList());
    }

    @Nullable
    private URL getNewBaseUrl(URL baseUrl, String url) {
        String combined = URLFactory.combine(baseUrl, url);
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
    public InputFormatObserver getObserver(@NonNull final InputFormatObserver inner, @NonNull final SourceReference sourceReference) {
        return null;
    }

    private String getPath(SourceReference reference, String key) {
        LinkedHashMap<String, Object> mapping = getMapping(reference);
        return (String) Optional.ofNullable(mapping.get(key)).orElse("");
    }

    private Map<String, List<Function<String, String>>> getPaths(SourceReference reference, String key, URL baseUrl) {
        LinkedHashMap<String, Object> mapping = getMapping(reference);
        Map<String, String> stringStringMap = (Map<String, String>) mapping.get(key);
        Map<String, List<Function<String, String>>> paths = new LinkedHashMap<>();
        if (stringStringMap != null) {
            try {
                for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
                    String key1 = entry.getKey();
                    String value = entry.getValue();
                    paths.put(key1, functionFactory.asFunctions(value, baseUrl));
                }
            } catch (Exception e) {
                throw new ProcessingException(reference, "Could not parse json handling for key " + key);
            }
        }
        return paths;
    }

    private LinkedHashMap<String, Object> getMapping(SourceReference reference) {
        LinkedHashMap<String, Object> mapping = (LinkedHashMap<String, Object>) reference.getProperty("mapping");
        if (mapping == null) {
            return new LinkedHashMap<>();
        }
        return mapping;
    }


}
