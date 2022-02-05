package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.search.ComponentMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Examines the labels of an item for parts that might be an url and could point to targets in the landscape.
 *
 * @todo collect hints and display to user instead of directly linking things
 */
public class LabelRelationResolver extends Resolver {

    private final HintFactory hintFactory;

    protected LabelRelationResolver(ProcessLog processLog, HintFactory hintFactory) {
        super(processLog);
        this.hintFactory = hintFactory;
    }

    @Override
    public void resolve(LandscapeDescription input) {

        List<Function<String, Boolean>> blacklistSpecs = getBlacklistSpecs(input.getConfig().getLabelBlacklist());

        for (ItemDescription description : input.getItemDescriptions()) {
            getHints(input, description, blacklistSpecs)
                    .forEach(hint -> applyDescriptionsFromHint(input.getItemDescriptions(), hint.getCreatedOrModifiedDescriptions()));
        }
    }

    private List<Hint> getHints(
            LandscapeDescription landscape,
            ItemDescription itemDescription,
            List<Function<String, Boolean>> blacklistSpecs
    ) {
        return itemDescription.getLabels().entrySet().stream()
                //skip the blacklisted labels
                .filter(entry -> blacklistSpecs.stream().noneMatch(spec -> spec.apply(entry.getKey())))
                .filter(entry -> StringUtils.hasLength(entry.getValue()))
                .map(entry -> hintFactory.createForLabel(landscape, itemDescription, entry.getKey()))
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
                processLog.warn("Failed to compile group matcher pattern " + s);
                return (Function<String, Boolean>) s1 -> s1.contains(s);
            }

        }).collect(Collectors.toList());
    }


    /**
     * Every hint can produce item descriptions to update existing items or lead to new ones.
     *
     * @param itemDescriptions current descriptions
     * @param hintDescriptions hint
     */
    private void applyDescriptionsFromHint(Set<ItemDescription> itemDescriptions, List<ItemDescription> hintDescriptions) {
        hintDescriptions.forEach(hintDescription -> {
            ComponentMatcher matcher = ComponentMatcher.forTarget(hintDescription.getFullyQualifiedIdentifier());
            Optional<ItemDescription> target =itemDescriptions.stream()
                    .filter(dto -> matcher.isSimilarTo(dto.getFullyQualifiedIdentifier()))
                    .findFirst();
            if (target.isPresent()) {
                target.get().assignSafeNotNull(hintDescription);
            } else {
                itemDescriptions.add(hintDescription);
            }
        });
    }
}
