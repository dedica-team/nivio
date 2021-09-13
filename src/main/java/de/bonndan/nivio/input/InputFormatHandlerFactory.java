package de.bonndan.nivio.input;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This service returns the correct input format (nivio, k8s...) handler for a {@link SourceReference}.
 */
@Service
public class InputFormatHandlerFactory {

    private final Map<InputFormatHandler, List<String>> factoryListMap = new ConcurrentHashMap<>();

    /**
     * The available handlers are injected here.
     *
     * @param inputFormatHandlers all available format handlers
     */
    public InputFormatHandlerFactory(List<InputFormatHandler> inputFormatHandlers) {
        inputFormatHandlers.forEach(itemDescriptionFactory -> factoryListMap.put(itemDescriptionFactory, itemDescriptionFactory.getFormats()));
    }

    /**
     * Returns the proper factory to generate/parse service descriptions based on the input format.
     *
     * @param reference the reference pointing at a file or url
     * @return the factory
     */
    @NonNull
    public InputFormatHandler getInputFormatHandler(@NonNull final SourceReference reference) {

        List<InputFormatHandler> factories = new ArrayList<>();
        factoryListMap.entrySet().stream()
                .filter(entry -> entry.getValue().stream().map(s -> {
                    if (!StringUtils.hasLength(s))
                        return "";
                    return s.toLowerCase();
                }).anyMatch(s -> s.equalsIgnoreCase(reference.getFormat()) || (!StringUtils.hasLength(s) && !StringUtils.hasLength(reference.getFormat()))))
                .forEach(entry -> factories.add(entry.getKey()));

        if (factories.isEmpty()) {
            List<String> knownFormats = new ArrayList<>();
            factoryListMap.values().forEach(knownFormats::addAll);
            String msg = String.format("Unknown source reference format: '%s', known formats are: '%s'",
                    reference.getFormat(),
                    StringUtils.collectionToDelimitedString(knownFormats, ", ")
            );
            throw new ProcessingException(msg);
        }

        //last one wins
        return factories.get(factories.size() - 1);
    }
}
