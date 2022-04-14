package de.bonndan.nivio.input.hints;

import de.bonndan.nivio.input.LabelToFieldResolver;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.Resolver;
import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.search.ComponentMatcher;
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
 * Does not modify the input, only writes to the {@link ProcessLog}.
 */
public class HintResolver implements Resolver {

    private final HintFactory hintFactory;

    public HintResolver(@NonNull final HintFactory hintFactory) {
        this.hintFactory = hintFactory;
    }

    @NonNull
    public LandscapeDescription resolve(@NonNull final LandscapeDescription input) {

        final List<Function<String, Boolean>> blacklistSpecs = getBlacklistSpecs(input.getConfig().getLabelBlacklist());

        Map<URI, List<Hint>> hints = new HashMap<>();
        final IndexReadAccess<ComponentDescription> readAccess = input.getReadAccess();

        readAccess.all(ItemDescription.class)
                .forEach(dto -> {
                    List<Hint> itemHints = new ArrayList<>();
                    itemHints.addAll(getHints(readAccess, dto, blacklistSpecs));
                    itemHints.addAll(getRelationHints(dto, input));
                    if (!itemHints.isEmpty()) {
                        hints.put(dto.getFullyQualifiedIdentifier(), itemHints);
                    }
                });

        input.getProcessLog().setHints(hints);
        return input;
    }

    private List<Hint> getHints(final IndexReadAccess<ComponentDescription> readAccess,
                                ItemDescription item,
                                List<Function<String, Boolean>> blacklistSpecs
    ) {

        return item.getLabels().entrySet().stream()
                //skip the blacklisted labels
                .filter(entry -> blacklistSpecs.stream().noneMatch(spec -> spec.apply(entry.getKey())))
                .filter(entry -> StringUtils.hasLength(entry.getValue()))
                .map(entry -> hintFactory.createForLabel(readAccess, item, entry.getKey()).orElse(null))
                .filter(Objects::nonNull)
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

    private List<Hint> getRelationHints(ItemDescription description, LandscapeDescription input) {

        List<Hint> hints = new ArrayList<>();
        var smartSearch = FlexSearch.forClassOn(ItemDescription.class, input.getReadAccess());

        //providers
        description.getProvidedBy().forEach(term -> {
            if (smartSearch.search(term.toLowerCase()).isEmpty()) {
                hints.add(hintFactory.createForTarget(description, RelationType.PROVIDER, term));
            }
        });

        //other relations
        description.getRelations().forEach(rel -> {
            //inverse links, e.g. from docker compose
            if (rel.getTarget() == null) {
                input.getProcessLog().warn(String.format("Found relation %s without target", rel));
                return;
            }
            String target = rel.getTarget().equalsIgnoreCase(description.getIdentifier()) ?
                    rel.getSource() : rel.getTarget();
            if (StringUtils.hasLength(target) && !hasTarget(target.toLowerCase(), input.getReadAccess(), input.getProcessLog())) {
                input.getProcessLog().info(String.format("%s: hints to a new target '%s'.", description, target.toLowerCase()));
                hints.add(hintFactory.createForTarget(description, rel.getType(), rel.getTarget()));
            }
        });

        return hints;
    }

    private boolean hasTarget(String term, IndexReadAccess<ComponentDescription> allItems, ProcessLog processLog) {

        Collection<ItemDescription> result = allItems.match(ComponentMatcher.forComponent(term), ItemDescription.class);
        if (result.size() > 1) {
            processLog.warn("Found ambiguous sources matching " + term);
            return true;
        }

        return !result.isEmpty();
    }
}
