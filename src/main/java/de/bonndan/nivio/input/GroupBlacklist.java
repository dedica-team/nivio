package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
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
class GroupBlacklist extends Resolver {

    private final List<Function<String, Boolean>> specs;

    GroupBlacklist(@NonNull final ProcessLog processLog, @NonNull final List<String> groupBlacklist) {
        super(processLog);
        specs = getSpecs(groupBlacklist);
    }

    @Override
    public void resolve(@NonNull final LandscapeDescription input) {
        filterGroups(input);
        filterItems(input);
    }

    /**
     * Blacklists groups and removes items from the input which are part of a blacklisted group.
     */
    private void filterGroups(@NonNull final LandscapeDescription input) {
        Set<GroupDescription> groupDescriptions = input.getIndexReadAccess().all(GroupDescription.class);
        var toDelete = groupDescriptions.stream()
                .filter(e -> isBlacklisted(e.getIdentifier()))
                .collect(Collectors.toList());

        toDelete.forEach(dto -> input.getWriteAccess().removeChild(dto));
    }

    /**
     * Blacklists groups and removes items from the input which are part of a blacklisted group.
     *
     */
    private void filterItems(@NonNull final LandscapeDescription input) {
        input.getIndexReadAccess().all(ItemDescription.class).stream()
                .filter(dto -> isBlacklisted(dto.getGroup()))
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

    private boolean isBlacklisted(String group) {
        return specs.stream().anyMatch(spec -> spec.apply(group));
    }
}
