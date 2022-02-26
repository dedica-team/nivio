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
public class HintResolver extends Resolver {

    private final HintFactory hintFactory;

    public HintResolver(@NonNull final HintFactory hintFactory, @NonNull final ProcessLog log) {
        super(log);
        this.hintFactory = hintFactory;
    }

    public void resolve(@NonNull final LandscapeDescription landscape) {

        final List<Function<String, Boolean>> blacklistSpecs = getBlacklistSpecs(landscape.getConfig().getLabelBlacklist());

        Map<URI, List<Hint>> hints = new HashMap<>();
        final IndexReadAccess<ComponentDescription> readAccess = landscape.getReadAccess();

        readAccess.all(ItemDescription.class)
                .forEach(dto -> {
                    List<Hint> itemHints = new ArrayList<>();
                    itemHints.addAll(getHints(readAccess, dto, blacklistSpecs));
                    itemHints.addAll(getRelationHints(dto, readAccess));
                    if (!itemHints.isEmpty()) {
                        hints.put(dto.getFullyQualifiedIdentifier(), itemHints);
                    }
                });

        processLog.setHints(hints);
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

    private List<Hint> getRelationHints(ItemDescription description, IndexReadAccess<ComponentDescription> readAccess) {

        List<Hint> hints = new ArrayList<>();
        //providers
        description.getProvidedBy().forEach(term -> {
            Optional<ItemDescription> provider = readAccess.matchOrSearchByIdentifierOrName(term.toLowerCase(), ItemDescription.class).stream().findFirst();

            if (provider.isEmpty()) {
                hints.add(hintFactory.createForTarget(description, RelationType.PROVIDER, term));
            }
        });

        //other relations
        description.getRelations().forEach(rel -> {
            //inverse links, e.g. from docker compose
            if (rel.getTarget() == null) {
                processLog.warn(String.format("Found relation %s without target", rel));
                return;
            }
            String target = rel.getTarget().equalsIgnoreCase(description.getIdentifier()) ?
                    rel.getSource() : rel.getTarget();
            if (StringUtils.hasLength(target) && !hasTarget(target.toLowerCase(), readAccess)) {
                processLog.info(String.format("%s: creating a new target item '%s' instantly.", description, target.toLowerCase()));
                hints.add(hintFactory.createForTarget(description, rel.getType(), rel.getTarget()));
            }
        });

        return hints;
    }

    private boolean hasTarget(String term, IndexReadAccess<ComponentDescription> allItems) {

        Collection<ItemDescription> result = allItems.match(ComponentMatcher.forTarget(term), ItemDescription.class);
        if (result.size() > 1) {
            processLog.warn("Found ambiguous sources matching " + term);
            return true;
        }

        return !result.isEmpty();
    }
}
