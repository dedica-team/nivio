package de.bonndan.nivio.input;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Examines the labels of an item for parts that might be an url and could point to targets in the landscape.
 *
 *
 */
public class HintProcessor {

    private final HintFactory hintFactory;

    public HintProcessor(HintFactory hintFactory) {
        this.hintFactory = hintFactory;
    }

    public void process(@NonNull final Landscape landscape) {

        final List<Function<String, Boolean>> blacklistSpecs = getBlacklistSpecs(landscape.getConfig().getLabelBlacklist());

        Map<URI, List<Hint>> hints = new HashMap<>();
        landscape.getIndexReadAccess().all(Item.class)
                .forEach(item -> hints.put(item.getFullyQualifiedIdentifier(), getHints(landscape, item, blacklistSpecs)));

        landscape.getLog().setHints(hints);
    }

    private List<Hint> getHints(Landscape landscape, Item item, List<Function<String, Boolean>> blacklistSpecs) {
        return item.getLabels().entrySet().stream()
                //skip the blacklisted labels
                .filter(entry -> blacklistSpecs.stream().noneMatch(spec -> spec.apply(entry.getKey())))
                .filter(entry -> StringUtils.hasLength(entry.getValue()))
                .map(entry -> hintFactory.createForLabel(landscape.getIndexReadAccess(), item, entry.getKey()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * @param configuredBlacklist configured matchers
     * @return functions to filter labels
     */
    private List<Function<String, Boolean>> getBlacklistSpecs(List<String> configuredBlacklist) {

        List<String> blacklist = new ArrayList<>();
        blacklist.add(Label.fill.name());
        blacklist.add(LabelToFieldResolver.LINK_LABEL_PREFIX + "*");
        blacklist.addAll(configuredBlacklist);

        return blacklist.stream().map(s -> {
            try {
                Pattern p = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                return (Function<String, Boolean>) s1 -> p.matcher(s1).matches();
            } catch (Exception e) {
                return (Function<String, Boolean>) s1 -> s1.contains(s);
            }

        }).collect(Collectors.toList());
    }

}
