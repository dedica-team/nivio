package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ItemDescriptionFormatFactory {

    private final Map<ItemDescriptionFactory, List<String>> factoryListMap = new ConcurrentHashMap<>();

    public static ItemDescriptionFormatFactory with(ItemDescriptionFactory factory) {
        return new ItemDescriptionFormatFactory(new ArrayList<>(Collections.singletonList(factory)));
    }

    public ItemDescriptionFormatFactory(List<ItemDescriptionFactory> factories) {
        factories.forEach(itemDescriptionFactory -> factoryListMap.put(itemDescriptionFactory, itemDescriptionFactory.getFormats()));
    }

    /**
     * Returns the proper factory to generate/parse service descriptions based on the input format.
     *
     * @param reference            the reference pointing at a file or url
     * @param landscapeDescription landscape, may contain a base url
     * @return the factory
     */
    public ItemDescriptionFactory getFactory(SourceReference reference, LandscapeDescription landscapeDescription) {

        List<ItemDescriptionFactory> factories = new ArrayList<>();
        factoryListMap.entrySet().stream()
                .filter(entry -> entry.getValue().stream().map(s -> {
                    if (StringUtils.isEmpty(s))
                        return "";
                    return s.toLowerCase();
                }).anyMatch(s -> s.equals(reference.getFormat()) || (StringUtils.isEmpty(s) && StringUtils.isEmpty(reference.getFormat()))))
                .forEach(entry -> factories.add(entry.getKey()));

        if (factories.isEmpty()) {
            List<String> knownFormats = new ArrayList<>();
            factoryListMap.values().forEach(knownFormats::addAll);
            String msg = "Unknown source reference format: '" + reference.getFormat() + "', known formats are: "
                    + StringUtils.collectionToDelimitedString(knownFormats, ", ");
            throw new ProcessingException(landscapeDescription, msg);
        }

        //last one wins
        return factories.get(factories.size() - 1);
    }
}
