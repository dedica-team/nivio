package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.GroupDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static de.bonndan.nivio.util.SafeAssign.assignSafeIfAbsent;
import static org.springframework.util.StringUtils.isEmpty;

public class GroupFactory {

    /**
     * Merges all absent values from the second param into the first.
     */
    public static void merge(final Group group, Group groupItem) {
        if (groupItem == null) {
            return;
        }

        assignSafeIfAbsent(groupItem.getColor(), group.getColor(), group::setColor);
        assignSafeIfAbsent(groupItem.getContact(), group.getContact(), group::setContact);
        assignSafeIfAbsent(groupItem.getDescription(), group.getDescription(), group::setDescription);
        assignSafeIfAbsent(groupItem.getOwner(), group.getOwner(), group::setOwner);
        groupItem.getLinks().forEach((s, url) -> group.getLinks().putIfAbsent(s, url));
        Labeled.merge(groupItem, group);
    }

    public static void mergeWithGroupDescription(final Group group, GroupDescription groupDescription) {
        if (groupDescription == null)
            return;

        assignSafeIfAbsent(groupDescription.getColor(), group.getColor(), group::setColor);
        assignSafeIfAbsent(groupDescription.getContact(), group.getContact(), group::setContact);
        assignSafeIfAbsent(groupDescription.getDescription(), group.getDescription(), group::setDescription);
        assignSafeIfAbsent(groupDescription.getOwner(), group.getOwner(), group::setOwner);
        groupDescription.getLinks().forEach((s, url) -> group.getLinks().putIfAbsent(s, url));
        Labeled.merge(groupDescription, group);
    }

}
