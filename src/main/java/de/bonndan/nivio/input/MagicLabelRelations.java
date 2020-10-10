package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.*;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Examines the labels of an item for parts that might be an url or parts of it and could point to targets in the landscape.
 *
 * Every label is split into parts and these parts are matched against landscape item names or identifiers.
 *
 */
public class MagicLabelRelations extends Resolver {

    /**
     * this could be made configurable later
     */
    private static final List<String> URL_PARTS = Arrays.asList("uri", "url", "host");

    private static final List<String> PROVIDER_INDICATORS = Arrays.asList("db", "database", "provider");
    public static final String KEY_SEPARATOR = "_";

    protected MagicLabelRelations(ProcessLog processLog) {
        super(processLog);
    }

    public void process(LandscapeDescription input, LandscapeImpl landscape) {

        Map<ItemDescription, List<LabelMatch>> itemMatches = new HashMap<>();
        List<Function<String, Boolean>> blacklistSpecs = getBlacklistSpecs(landscape.getConfig().getLabelBlacklist());

        input.getItemDescriptions().all().forEach(item -> itemMatches.put(item, getMatches(item, landscape, blacklistSpecs)));

        //search for targets in the landscape, i.e. where name or identifier of an item matches the "possible targets"
        itemMatches.forEach((item, labelMatches) -> {
            labelMatches.forEach(labelMatch -> {
                labelMatch.possibleTargets.forEach(toFind -> {
                    String s = landscape.getItems().selectByIdentifierOrName(toFind);
                    Collection<? extends LandscapeItem> possibleTargets = landscape.getItems().cqnQueryOnIndex(s);

                    if (possibleTargets.size() != 1) {
                        processLog.debug("Found no target of magic relation from item " + item.getIdentifier() + " using '" + toFind + "'");
                        return;
                    }

                    String source = item.getIdentifier();
                    String target = possibleTargets.iterator().next().getIdentifier();
                    processLog.info("Found a target of magic relation from "
                            + item.getIdentifier() + "(" + item.getName() + ")"
                            + " to target '" + target + "' using '" + toFind + "'");
                    boolean relationExists = item.getRelations().stream()
                            .anyMatch(r -> hasRelation(source, target, r));
                    boolean isEqual = source.equalsIgnoreCase(target);
                    if (!relationExists && !isEqual) {
                        RelationDescription relation = new RelationDescription(source, target);
                        //inverse
                        if (isProvider(labelMatch)) {
                            relation = new RelationDescription(target, source);
                            relation.setType(RelationType.PROVIDER);
                        }
                        item.addRelation(relation);
                        return;
                    }

                    processLog.debug("Relation between " + source + " and " + target + " already exists, not adding magic one.");
                });
            });
        });
    }

    private boolean isProvider(LabelMatch labelMatch) {
        List<String> labelParts = Arrays.stream(labelMatch.key.split(KEY_SEPARATOR))
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return labelParts.stream().anyMatch(PROVIDER_INDICATORS::contains);
    }

    private boolean hasRelation(String source, String target, RelationDescription r) {
        return r.getSource().equals(source) && r.getTarget().equals(target) ||
                r.getSource().equals(target) && r.getTarget().equals(source);
    }

    private List<LabelMatch> getMatches(
            ItemDescription itemDescription,
            LandscapeImpl landscape,
            List<Function<String, Boolean>> blacklistSpecs
    ) {
        return itemDescription.getLabels().entrySet().stream()
                //skip the blacklisted labels
                .filter(entry -> blacklistSpecs.stream().noneMatch(spec -> spec.apply(entry.getKey())))
                .map(entry -> getPossibleTargetsForLabel(entry.getKey(), entry.getValue(), landscape))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private LabelMatch getPossibleTargetsForLabel(String key, String value, LandscapeImpl landscape) {

        List<String> keyParts = Arrays.stream(key.split(KEY_SEPARATOR))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        if (URL_PARTS.stream().noneMatch(keyParts::contains)) {
            return null;
        }

        List<String> aliasesToFind = new ArrayList<>(keyParts);
        if (!StringUtils.isEmpty(value)) {
            try {
                URL url = new URL(value);
                aliasesToFind.add(url.getHost());
                aliasesToFind.addAll(Arrays.asList(url.getPath().split("/"))); //add all path parts
            } catch (MalformedURLException ignored) {
                Optional<Item> valueMatch = landscape.getItems().find(ItemMatcher.forTarget(value));
                if (valueMatch.isPresent()) {
                    aliasesToFind.clear();
                    aliasesToFind.add(ItemMatcher.forTarget(valueMatch.get()).toString());
                } else {
                    aliasesToFind.addAll(Arrays.asList(value.split(":")));
                }
            }
        }

        //filter empty string and indicators ("db", "host") from possible targets
        Set<String> collect = aliasesToFind.stream()
                .filter(s -> !StringUtils.isEmpty(s))
                .filter(s -> !URL_PARTS.contains(s.toLowerCase()) && !PROVIDER_INDICATORS.contains(s.toLowerCase()))
                .collect(Collectors.toSet());
        return new LabelMatch(key, value, collect);
    }

    private static class LabelMatch {
        String key;
        String value;
        private final Set<String> possibleTargets;

        LabelMatch(String key, String value, Set<String> possibleTargets) {
            this.key = key;
            this.value = value;
            this.possibleTargets = possibleTargets;
        }
    }

    private List<Function<String, Boolean>> getBlacklistSpecs(List<String> blacklist) {
        return blacklist.stream().map(s -> {
            try {
                Pattern p = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                return (Function<String, Boolean>) s1 -> p.matcher(s1).matches();
            } catch (Exception e) {
                processLog.warn("Failed to compile group matcher pattern " + s);
                return (Function<String, Boolean>) s1 -> s1.contains(s);
            }

        }).collect(Collectors.toList());
    }
}
