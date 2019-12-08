package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Examines the labels of an item for parts that point to being an url and could point to targets in the landscape.
 *
 *
 *
 */
public class MagicLabelRelations {

    private static final Logger LOGGER = LoggerFactory.getLogger(MagicLabelRelations.class);

    private static final List<String> urlParts = Arrays.asList("uri", "url", "host");

    public void process(LandscapeDescription input, LandscapeImpl landscape) {

        Map<ItemDescription, Set<String>> itemMatches = new HashMap<>();
        input.getItemDescriptions().forEach(itemDescription -> {
            Set<String> urlMatches = parseLabels(itemDescription);
            itemMatches.put(itemDescription, urlMatches);
        });

        //search for targets in the landscape
        itemMatches.forEach((key, value) -> value.forEach(toFind -> {

            //TODO this is very ineffective, create an indexed collection once.
            Collection<? extends LandscapeItem> possibleTargets = Items.cqnQuery("SELECT * FROM items WHERE (identifier = '"  + toFind  + "' OR name ='"  + toFind  + "')", landscape.getItems());

            if (possibleTargets.size() == 1) {
                String target = possibleTargets.iterator().next().getIdentifier();
                LOGGER.info("Found a target of magic relation from {} to {} using '{}'", key.getIdentifier(), target, toFind);
                key.addRelation(new RelationDescription(key.getIdentifier(), target));
            } else {
                LOGGER.trace("Found no target of magic relation from item {} using '{}'", key.getIdentifier(), toFind);
            }
        }));
    }

    private Set<String> parseLabels(ItemDescription itemDescription) {
        return itemDescription.getLabels().entrySet().stream()
                .map(entry -> getUrlMatches(entry.getKey(), entry.getValue()))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(s -> !StringUtils.isEmpty(s))
                .collect(Collectors.toSet());
    }

    private List<String> getUrlMatches(String key, String value) {
        List<String> keyParts = Arrays.stream(key.split("_")).map(String::toLowerCase).collect(Collectors.toList());
        if (urlParts.stream().noneMatch(keyParts::contains)) {
            return null;
        }

        List<String> aliasesToFind = new ArrayList<>();
        aliasesToFind.addAll(keyParts);
        try {
            URL url = new URL(value);
            aliasesToFind.add(url.getHost());
            aliasesToFind.addAll(Arrays.asList(url.getPath().split("/"))); //add all path parts
        } catch (MalformedURLException ignored) {
            aliasesToFind.add(value);
        }

        return aliasesToFind;
    }
}
