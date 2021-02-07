package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
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
public class GroupProcessor extends Processor {

    protected GroupProcessor(ProcessLog processLog) {
        super(processLog);
    }

    public void process(LandscapeDescription landscapeDescription, Landscape landscape) {
        List<Function<String, Boolean>> specs = getSpecs(landscapeDescription.getConfig().getGroupBlacklist());

        landscapeDescription.getGroupDescriptions().forEach((groupIdentifier, groupDescription) -> {

            if (isNotBlacklisted(groupIdentifier, specs)) {
                processLog.info("Adding or updating group " + groupIdentifier);
                Set<Item> items = landscape.getItems().all().stream()
                        .filter(item -> item.getGroup().equals(groupIdentifier))
                        .collect(Collectors.toUnmodifiableSet());
                landscape.addGroup(GroupFactory.createFromDescription(
                        groupIdentifier, landscape.getIdentifier(),
                        groupDescription, items
                ));
            } else {
                processLog.info("Ignoring blacklisted group " + groupIdentifier);
           }
        });

        ArrayList<ItemDescription> copy = new ArrayList<>(landscapeDescription.getItemDescriptions().all());
        copy.forEach(itemDescription -> {

            String groupIdentifier = itemDescription.getGroup();
            if (StringUtils.isEmpty(itemDescription.getGroup())) {
                groupIdentifier = Group.COMMON;
            }

            if (isNotBlacklisted(groupIdentifier, specs)) {
                if (!landscape.getGroups().containsKey(groupIdentifier)) {
                    landscape.addGroup(GroupFactory.createFromDescription(
                            groupIdentifier, landscape.getIdentifier(), null,
                            Set.of(ItemFactory.fromDescription(itemDescription, landscape))
                    ));
                }
            } else {
                processLog.info("Removing item " + itemDescription.getIdentifier() + " because in blacklisted group " + groupIdentifier);
                landscapeDescription.getItemDescriptions().remove(itemDescription);
            }
        });
    }

    private List<Function<String, Boolean>> getSpecs(List<String> blacklist) {
        return blacklist.stream().map(s -> {
            try {
                Pattern p = Pattern.compile(s);
                return (Function<String, Boolean>) s1 -> p.matcher(s1).matches();
            } catch (Exception e) {
                processLog.warn("Failed to compile group matcher pattern " + s);
                return (Function<String, Boolean>) s1 -> s1.contains(s);
            }

        }).collect(Collectors.toList());
    }

    private boolean isNotBlacklisted(String groupIdentifier, List<Function<String, Boolean>> specs) {
        return specs.stream().noneMatch(spec -> spec.apply(groupIdentifier));
    }

}
