package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeDescriptionFactory;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Resolves the groups in the landscape by examining item.group names and adds missing (not pre-configured) groups.
 *
 * Blacklists groups and removes items from the input which are part of a blacklisted group.
 */
class GroupBlacklist implements Resolver {

    @NonNull
    @Override
    public LandscapeDescription resolve(@NonNull final LandscapeDescription input) {
        final var specs = getSpecs(input.getConfig().getGroupBlacklist());
        filterGroups(input, specs);
        filterItems(input, specs);
        return LandscapeDescriptionFactory.refreshedCopyOf(input);
    }

    /**
     * Blacklists groups and removes items from the input which are part of a blacklisted group.
     */
    private void filterGroups(@NonNull final LandscapeDescription input, final List<Function<String, Boolean>> specs) {
        Set<GroupDescription> groupDescriptions = input.getReadAccess().all(GroupDescription.class);
        var toDelete = groupDescriptions.stream()
                .filter(e -> specs.stream().anyMatch(spec -> spec.apply(e.getIdentifier())))
                .collect(Collectors.toList());

        toDelete.forEach(dto -> input.getWriteAccess().removeChild(dto));
    }

    /**
     * Blacklists groups and removes items from the input which are part of a blacklisted group.
     */
    private void filterItems(@NonNull final LandscapeDescription input, final List<Function<String, Boolean>> specs) {
        input.getReadAccess().all(ItemDescription.class).stream()
                .filter(dto -> specs.stream().anyMatch(spec -> spec.apply(dto.getGroup())))
                .forEach(dto -> input.getWriteAccess().removeChild(dto));
    }

    private List<Function<String, Boolean>> getSpecs(List<String> blacklist) {
        return blacklist.stream().map(s -> {
            try {
                Pattern p = Pattern.compile(s);
                return (Function<String, Boolean>) s1 -> p.matcher(s1).matches();
            } catch (Exception e) {
                return (Function<String, Boolean>) s1 -> s1.contains(s);
            }

        }).collect(Collectors.toList());
    }

}
