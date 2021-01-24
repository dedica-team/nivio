package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.ItemMatcher;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.util.URIHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HintFactory {

    public static final String KEY_SEPARATOR = "_";
    private static final Logger LOGGER = LoggerFactory.getLogger(HintFactory.class);

    /**
     * this could be made configurable later
     */
    private static final List<String> URL_PARTS = Arrays.asList("uri", "url", "host");

    private static final Map<String, Supplier<Hint>> uriHints = new HashMap<>();

    static {
        uriHints.put("mysql", () -> hintsTo(ItemType.DATABASE, RelationType.PROVIDER));
        uriHints.put("mongodb", () -> hintsTo(ItemType.DATABASE, RelationType.PROVIDER, "MongoDB"));
        uriHints.put("http", () -> hintsTo(RelationType.DATAFLOW));
        uriHints.put("https", () -> hintsTo(RelationType.DATAFLOW));
        uriHints.put("jdbc", () -> hintsTo(ItemType.DATABASE, RelationType.PROVIDER));
        uriHints.put("redis", () -> hintsTo(ItemType.KEYVALUESTORE, RelationType.PROVIDER, "Redis"));
        uriHints.put("rediss", () -> hintsTo(ItemType.KEYVALUESTORE, RelationType.PROVIDER));
        uriHints.put("smb", () -> hintsTo(ItemType.VOLUME, RelationType.PROVIDER));
    }

    private static Hint hintsTo(String itemType) {
        return hintsTo(itemType, null, null);
    }

    private static Hint hintsTo(RelationType relationType) {
        return hintsTo(null, relationType, null);
    }

    private static Hint hintsTo(String itemType, RelationType relationType) {
        return new Hint(itemType, relationType, null);
    }

    private static Hint hintsTo(String itemType, RelationType relationType, String software) {
        return new Hint(itemType, relationType, software);
    }

    /**
     * Create a new hint for a relation to a different/new landscape item.
     *
     * @param landscape landscape description
     * @param item      the item the hint is created for
     * @param key       label key
     * @param value     label value
     * @return a hint if any label part could be used
     */
    public Optional<Hint> create(LandscapeDescription landscape, ItemDescription item, String key, String value) {

        List<String> keyParts = Arrays.stream(key.split(KEY_SEPARATOR))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        boolean hasUrlLikeKey = URL_PARTS.stream().anyMatch(keyParts::contains);
        Optional<URI> optionalURI = URIHelper.getURIWithHostAndScheme(value);

        if (!hasUrlLikeKey && optionalURI.isEmpty()) {
            return Optional.empty();
        }

        ItemDescription target = getTarget(landscape.getItemDescriptions(), value, optionalURI).orElseGet(() -> {
            ItemDescription createdTarget = new ItemDescription();
            createdTarget.setIdentifier(value);
            createdTarget.setName(value);
            optionalURI.ifPresent(uri -> {
                createdTarget.setAddress(uri.toString());
            });
            return createdTarget;
        });


        LOGGER.info(String.format("Found a target of relation from %s(%s) to target '%s' using '%s'", item.getIdentifier(), item.getName(), target, value));

        if (item.getIdentifier().equalsIgnoreCase(target.getIdentifier())) {
            return Optional.empty();
        }

        //get a hint based on uri scheme
        Hint hint = uriHints.getOrDefault(optionalURI.map(URI::getScheme).orElse(""), Hint::new).get();

        Optional<RelationDescription> relationDescription = item.getRelations().stream()
                .filter(r -> r.getSource().equals(item.getIdentifier()) && r.getTarget().equals(target.getIdentifier()) ||
                        r.getSource().equals(target.getIdentifier()) && r.getTarget().equals(item.getIdentifier()))
                .findFirst();

        hint.use(item, target, relationDescription);
        return Optional.of(hint);
    }

    private static Optional<ItemDescription> getTarget(ItemDescriptions itemDescriptions, String value, Optional<URI> optionalURI) {

        return itemDescriptions.find(ItemMatcher.forTarget(value)).or(() -> {
            if (optionalURI.isPresent()) {
                String query = String.format("SELECT * WHERE address = '%s'", optionalURI.get());
                return itemDescriptions.query(query).stream().findFirst();
            }
            return Optional.empty();
        });
    }
}
