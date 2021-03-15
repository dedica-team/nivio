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
        if (a.isPresent() && b.isEmpty() || a.isEmpty() && b.isPresent()) {
            changes.add(key + " changed to: " + b.orElse(""));
            return;
        }
        if (!org.apache.commons.lang3.StringUtils.equals(a.map(Object::toString).orElse(""), b.map(Object::toString).orElse(""))) {
            changes.add(key + " changed to: " + b);
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
        Collection<String> disjunction = CollectionUtils.disjunction(one, two);
        String changedKeys = String.join(",", disjunction);
        if (!StringUtils.isEmpty(changedKeys)) {
            changes.add(String.format("%s have differences : '%s'", key, changedKeys));
        }
    }
}
