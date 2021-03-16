package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.GroupFactory;
import de.bonndan.nivio.model.Landscape;
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

    public ProcessingChangelog process(LandscapeDescription input, Landscape landscape) {

        List<Function<String, Boolean>> specs = getSpecs(input.getConfig().getGroupBlacklist());

        input.getGroups().forEach((identifier, groupDescription) -> {
            Group g = GroupFactory.createFromDescription(identifier, landscape.getIdentifier(), groupDescription);

            if (!isBlacklisted(g.getIdentifier(), specs)) {
                processLog.info("Adding or updating group " + g.getIdentifier());
                landscape.addGroup(g);
            } else {
                processLog.info("Ignoring blacklisted group " + g.getIdentifier());
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
                    landscape.addGroup(GroupFactory.createFromDescription(group, landscape.getIdentifier(), null));
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
                group.get().addItem(item);
                return;
            }
            if (isBlacklisted(item.getGroup(), specs)) {
                processLog.info(String.format("Deleting item of blacklisted group %s", item.getGroup()));
                landscape.getItems().remove(item);
                return;
            }
            throw new RuntimeException(String.format("item group '%s' not found.", item.getGroup()));
        });

        return new ProcessingChangelog();
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
