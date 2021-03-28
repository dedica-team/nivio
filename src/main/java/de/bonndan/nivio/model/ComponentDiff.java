package de.bonndan.nivio.model;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

public class ComponentDiff {

    /**
     * Compares changes in two strings.
     *
     * @param a       first
     * @param b       second
     * @param key     label
     * @param changes array of changes
     */
    public static void compareStrings(String a, String b, String key, List<String> changes) {
        if (!org.apache.commons.lang3.StringUtils.equals(a, b)) {
            changes.add(key + " changed to: " + b);
        }
    }

    /**
     * Compares changes in string representation of two objects
     *
     * @param a       first
     * @param b       second
     * @param key     label
     * @param changes array of changes
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static void compareOptionals(Optional<Object> a, Optional<Object> b, String key, List<String> changes) {
        if (a.isEmpty() && b.isEmpty()) {
            return;
        }

        boolean onlyAExists = a.isPresent() && b.isEmpty();
        //noinspection ConstantConditions left here for better readability
        boolean onlyBExists = a.isEmpty() && b.isPresent();

        if (onlyAExists || onlyBExists) {
            changes.add(String.format("%s changed to: %s", key, b.orElse("")));
            return;
        }

        String aString = a.map(Object::toString).orElse("");
        String bString = b.map(Object::toString).orElse("");
        if (!aString.equals(bString)) {
            changes.add(String.format("%s changed to: %s", key, bString));
        }
    }

    /**
     * Compares to string collections.
     *
     * @param one     first
     * @param two     second
     * @param key     label
     * @param changes list of changes
     */
    public static void compareCollections(Collection<String> one, Collection<String> two, String key, List<String> changes) {
        @SuppressWarnings("unchecked") Collection<String> disjunction = CollectionUtils.disjunction(one, two);
        String changedKeys = String.join(",", disjunction);
        if (!StringUtils.isEmpty(changedKeys)) {
            changes.add(String.format("%s have differences : '%s'", key, changedKeys));
        }
    }
}
