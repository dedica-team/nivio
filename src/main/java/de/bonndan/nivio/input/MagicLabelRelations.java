package de.bonndan.nivio.input;

import com.googlecode.cqengine.IndexedCollection;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Examines the labels of an item for parts that point to being an url and could point to targets in the landscape.
 */
public class MagicLabelRelations {

    private static final Logger LOGGER = LoggerFactory.getLogger(MagicLabelRelations.class);

    /**
     * this could be made configurable later
     */
    private static final List<String> URL_PARTS = Arrays.asList("uri", "url", "host");

    private static final List<String> PROVIDER_INDICATORS = Arrays.asList("db", "database", "provider");

    public void process(LandscapeDescription input, LandscapeImpl landscape) {

        Map<ItemDescription, List<LabelMatch>> itemMatches = new HashMap<>();
        input.getItemDescriptions().forEach(item -> itemMatches.put(item, getMatches(item)));

        //search for targets in the landscape
        IndexedCollection<LandscapeItem> index = Items.index(landscape.getItems());
        itemMatches.forEach((description, labelMatches) -> {
            labelMatches.forEach(labelMatch -> {
                labelMatch.possibleTargets.forEach(toFind -> {
                    Collection<? extends LandscapeItem> possibleTargets = Items.cqnQueryOnIndex(
                            "SELECT * FROM items WHERE (identifier = '" + toFind + "' OR name ='" + toFind + "')", index);

                    if (possibleTargets.size() != 1) {
                        LOGGER.debug("Found no target of magic relation from item {} using '{}'", description.getIdentifier(), toFind);
                        return;
                    }

                    String source = description.getIdentifier();
                    String target = possibleTargets.iterator().next().getIdentifier();
                    LOGGER.info("Found a target of magic relation from {} to {} using '{}'", description.getIdentifier(), target, toFind);
                    boolean relationExists = description.getRelations().stream()
                            .anyMatch(r -> hasRelation(source, target, r));
                    boolean isEqual = source.equals(target);
                    if (!relationExists && !isEqual) {
                        RelationDescription relation = new RelationDescription(source, target);
                        //inverse
                        if (isProvider(labelMatch)) {
                            relation = new RelationDescription(target, source);
                            relation.setType(RelationType.PROVIDER);
                        }
                        description.addRelation(relation);
                    } else {
                        LOGGER.info("Relation between {} and {} already exists, not adding magic one.", source, target);
                    }
                });
            });
        });
    }

    private boolean isProvider(LabelMatch labelMatch) {
        List<String> labelParts = Arrays.stream(labelMatch.key.split("_"))
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return labelParts.stream().anyMatch(PROVIDER_INDICATORS::contains);
    }

    private boolean hasRelation(String source, String target, RelationItem<String> r) {
        return r.getSource().equals(source) && r.getTarget().equals(target) ||
                r.getSource().equals(target) && r.getTarget().equals(source);
    }

    private List<LabelMatch> getMatches(ItemDescription itemDescription) {
        return itemDescription.getLabels().entrySet().stream()
                .map(entry -> getPossibleTargetsForLabel(entry.getKey(), entry.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private LabelMatch getPossibleTargetsForLabel(String key, String value) {
        List<String> keyParts = Arrays.stream(key.split("_")).map(String::toLowerCase).collect(Collectors.toList());
        if (URL_PARTS.stream().noneMatch(keyParts::contains)) {
            return null;
        }

        List<String> aliasesToFind = new ArrayList<>(keyParts);
        try {
            URL url = new URL(value);
            aliasesToFind.add(url.getHost());
            aliasesToFind.addAll(Arrays.asList(url.getPath().split("/"))); //add all path parts
        } catch (MalformedURLException ignored) {
            aliasesToFind.addAll(Arrays.asList(value.split(":")));
        }

        return new LabelMatch(key, value, aliasesToFind);
    }

    private static class LabelMatch {
        String key;
        String value;
        private final List<String> possibleTargets;
        RelationType relationType;

        LabelMatch(String key, String value, List<String> possibleTargets) {
            this.key = key;
            this.value = value;
            this.possibleTargets = possibleTargets;
        }
    }
}
