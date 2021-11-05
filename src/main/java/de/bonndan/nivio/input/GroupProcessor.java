package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.GroupFactory;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public ProcessingChangelog process(@NonNull final LandscapeDescription input, @NonNull final Landscape landscape) {

        ProcessingChangelog changelog = new ProcessingChangelog();
        List<Function<String, Boolean>> specs = getSpecs(input.getConfig().getGroupBlacklist());

        /*
         * this handles the configured groups, the default/fallback group COMMON is not configured
         */
        input.getGroups().forEach((identifier, groupDescription) -> {
            Group g = GroupFactory.createFromDescription(identifier, landscape.getIdentifier(), groupDescription);

            if (!isBlacklisted(g.getIdentifier(), specs)) {

                Optional<Group> existing = landscape.getGroup(g.getIdentifier());
                Group added = landscape.addGroup(g);
                if (existing.isEmpty()) {
                    processLog.info(String.format("Adding group %s", g.getIdentifier()));
                    changelog.addEntry(added, ProcessingChangelog.ChangeType.CREATED);
                } else {
                    processLog.info(String.format("Updating group %s", g.getIdentifier()));
                    String updates = existing.get().getChanges(added).isEmpty() ?
                            String.format("Item(s) changed in group '%s'", g.getIdentifier()) : String.join("; ", existing.get().getChanges(added));
                    changelog.addEntry(added, ProcessingChangelog.ChangeType.UPDATED, updates);
                }
            } else {
                processLog.info(String.format("Ignoring blacklisted group %s", g.getIdentifier()));
            }
        });

        ArrayList<ItemDescription> copy = new ArrayList<>(input.getItemDescriptions().all());
        copy.forEach(item -> {

            String group = item.getGroup();
            if (StringUtils.isEmpty(item.getGroup())) {
                group = Group.COMMON;
            }

            if (!isBlacklisted(group, specs)) {
                if (!landscape.getGroups().containsKey(group)) {
                    Group fromDescription = GroupFactory.createFromDescription(group, landscape.getIdentifier(), null);
                    changelog.addEntry(fromDescription, ProcessingChangelog.ChangeType.CREATED, String.format("Reference by item %s", item));
                    processLog.info("Adding group " + fromDescription.getIdentifier());
                    landscape.addGroup(fromDescription);
                }
            } else {
                processLog.info("Removing item " + item.getIdentifier() + " because in blacklisted group " + group);
                input.getItemDescriptions().remove(item);
            }
        });

        //assign each item to a group
        landscape.getItems().all().forEach(item -> {
            Optional<Group> group = landscape.getGroup(item.getGroup());
            if (group.isPresent()) {
                group.get().addOrReplaceItem(item);
                return;
            }
            if (isBlacklisted(item.getGroup(), specs)) {
                processLog.info(String.format("Deleting item of blacklisted group %s", item.getGroup()));
                landscape.getItems().remove(item);
                return;
            }
            throw new RuntimeException(String.format("item group '%s' not found.", item.getGroup()));
        });

        return changelog;
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

    private boolean isBlacklisted(String group, List<Function<String, Boolean>> specs) {
        return specs.stream().anyMatch(spec -> spec.apply(group));
    }

}
