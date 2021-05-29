package de.bonndan.nivio.model;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ComponentDiff {

    /**
     * Compares changes in two strings.
     *
     * @param a       first
     * @param b       second
     * @param key     label
     */
    public static List<String> compareStrings(@Nullable final String a, @Nullable final String b, @NonNull final String key) {
        if (!org.apache.commons.lang3.StringUtils.equals(a, b)) {
            return List.of(String.format("%s changed to: %s", key, b));
        }

        return Collections.emptyList();
    }

    /**
     * Compares changes in string representation of two objects
     *
     * @param a       first
     * @param b       second
     * @param key     label
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static List<String> compareOptionals(Optional<Object> a, Optional<Object> b, @NonNull final String key) {

        List<String> changes = new ArrayList<>();
        if (a.isEmpty() && b.isEmpty()) {
            return changes;
        }

        boolean onlyAExists = a.isPresent() && b.isEmpty();
        //noinspection ConstantConditions left here for better readability
        boolean onlyBExists = a.isEmpty() && b.isPresent();

        if (onlyAExists || onlyBExists) {
            changes.add(String.format("%s changed to: %s", key, b.orElse("")));
            return changes;
        }

        String aString = a.map(Object::toString).orElse("");
        String bString = b.map(Object::toString).orElse("");
        if (!aString.equals(bString)) {
            changes.add(String.format("%s changed to: %s", key, bString));
        }

        return changes;
    }

    /**
     * Compares to string collections.
     *
     * @param one     first
     * @param two     second
     * @param key     label
     */
    public static List<String> compareCollections(@NonNull final Collection<String> one,
                                                  @NonNull final Collection<String> two,
                                                  @NonNull final String key
    ) {
        @SuppressWarnings("unchecked") Collection<String> disjunction = CollectionUtils.disjunction(one, two);
        String changedKeys = disjunction.stream().filter(Objects::nonNull).collect(Collectors.joining(","));

        if (!StringUtils.isEmpty(changedKeys)) {
            return List.of(String.format("%s have differences: '%s'", key, changedKeys));
        }
        return Collections.emptyList();
    }
}
