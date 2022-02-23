package de.bonndan.nivio.input;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.search.ComponentMatcher;
import de.bonndan.nivio.util.URIHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HintFactory {

    public static final String KEY_SEPARATOR = "_";
    private static final Logger LOGGER = LoggerFactory.getLogger(HintFactory.class);

    /**
     * this could be made configurable later
     */
    private static final List<String> URL_PARTS = Arrays.asList("uri", "url", "host");

    private static final Map<String, Function<URI, Hint>> uriHints = new HashMap<>();

    static {
        uriHints.put("mysql", uri -> hintsTo(uri, ItemType.DATABASE, RelationType.PROVIDER));
        uriHints.put("mongodb", uri -> hintsTo(uri, ItemType.DATABASE, RelationType.PROVIDER, "MongoDB"));
        uriHints.put("http", uri -> hintsTo(uri, null, RelationType.DATAFLOW, null));
        uriHints.put("https", uri -> hintsTo(uri, null, RelationType.DATAFLOW, null));
        uriHints.put("jdbc", uri -> hintsTo(uri, ItemType.DATABASE, RelationType.PROVIDER));
        uriHints.put("redis", uri -> hintsTo(uri, ItemType.KEYVALUESTORE, RelationType.PROVIDER, "Redis"));
        uriHints.put("rediss", uri -> hintsTo(uri, ItemType.KEYVALUESTORE, RelationType.PROVIDER));
        uriHints.put("smb", uri -> hintsTo(uri, ItemType.VOLUME, RelationType.PROVIDER));
    }

    private static Hint hintsTo(URI uri, String itemType, RelationType relationType) {
        return new Hint(uri, itemType, relationType, null);
    }

    private static Hint hintsTo(URI uri, String itemType, RelationType relationType, String software) {
        return new Hint(uri, itemType, relationType, software);
    }

    /**
     * Create a new hint for a relation to a different/new landscape item.
     *
     * @param readAccess landscape read access
     * @param item       the item the hint is created for
     * @param labelKey   label key
     * @return a hint if any label part could be used
     */
    public Optional<Hint> createForLabel(@NonNull final IndexReadAccess<GraphComponent> readAccess,
                                         @NonNull final Item item,
                                         @NonNull final String labelKey
    ) {

        List<String> keyParts = Arrays.stream(labelKey.split(KEY_SEPARATOR))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        boolean hasUrlLikeKey = URL_PARTS.stream().anyMatch(keyParts::contains);

        String value = item.getLabel(labelKey);
        if (!StringUtils.hasLength(value)) {
            return Optional.empty();
        }
        Optional<URI> optionalURI = URIHelper.getURIWithHostAndScheme(value);

        if (!hasUrlLikeKey && optionalURI.isEmpty()) {
            return Optional.empty();
        }

        List<Item> targets = getTargets(readAccess, value, optionalURI);
        if (targets.size() > 1) {
            LOGGER.info("Found ambiguous results searching for target {}", value);
            return Optional.empty();
        }

        Item target = null;
        if (targets.size() == 1) {
            target = targets.get(0);
            LOGGER.info("Found a target of relation from {}({}) to target '{}' using {}: '{}'", item.getIdentifier(), item.getName(), target, labelKey, value);

            //get a hint based on uri scheme
            Item finalTarget = target;
            Optional<Relation> relation = item.getRelations().stream()
                    .filter(r -> r.getSource().equals(item) && r.getTarget().equals(finalTarget) ||
                            r.getSource().equals(finalTarget) && r.getTarget().equals(item))
                    .findFirst();

            if (relation.isPresent()) {
                return Optional.empty();
            }
        }

        Function<URI, Hint> hintFunction = uriHints.getOrDefault(optionalURI.map(URI::getScheme).orElse(""), (uri -> new Hint(uri, null, null, null)));
        Hint hint = hintFunction.apply(item.getFullyQualifiedIdentifier());
        if (target != null) {
            hint.setTarget(target.getFullyQualifiedIdentifier());
        }
        return Optional.of(hint);
    }

    private static List<Item> getTargets(IndexReadAccess<GraphComponent> readAccess, String value, Optional<URI> optionalURI) {

        List<Item> results = new ArrayList<>();
        if (optionalURI.isPresent()) {
            readAccess.searchAddress(optionalURI.get().toString(), Item.class).stream().findFirst().ifPresent(results::add);
            return results;
        }

        Collection<Item> targets = readAccess.matchOrSearchByIdentifierOrName(value, Item.class);
        if (targets.size() != 1) {
            LOGGER.debug("Found ambiguous results {} for query for target '{}'", targets, value);
        }
        results.addAll(targets);
        return results;
    }
}
