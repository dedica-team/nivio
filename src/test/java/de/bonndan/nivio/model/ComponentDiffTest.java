package de.bonndan.nivio.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentDiffTest {

    private List<String> changes;

    @BeforeEach
    void setup() {
        changes = new ArrayList<>();
    }

    @Test
    void twoNulls() {
        changes.addAll(ComponentDiff.compareStrings(null, null, "foo"));
        assertThat(changes).hasSize(0);
    }

    @Test
    void twoNull() {
        changes.addAll(ComponentDiff.compareStrings("a", null, "foo"));
        assertThat(changes).hasSize(1);
    }

    @Test
    void oneNull() {
        changes.addAll(ComponentDiff.compareStrings(null, "b", "foo"));
        assertThat(changes).hasSize(1);
    }

    @Test
    void similarString() {
        changes.addAll(ComponentDiff.compareStrings("b", "b", "foo"));
        assertThat(changes).hasSize(0);
    }

    @Test
    void differentString() {
        changes.addAll(ComponentDiff.compareStrings("a", "b", "foo"));
        assertThat(changes).hasSize(1);
    }

    @Test
    void bothEmpty() {
        changes.addAll(ComponentDiff.compareOptionals(Optional.empty(), Optional.empty(), "foo"));
        assertThat(changes).hasSize(0);
    }

    @Test
    void oneEmpty() {
        changes.addAll(ComponentDiff.compareOptionals(Optional.empty(), Optional.of("a"), "foo"));
        assertThat(changes).hasSize(1);
    }

    @Test
    void twoEmpty() {
        changes.addAll(ComponentDiff.compareOptionals(Optional.of("a"), Optional.empty(), "foo"));
        assertThat(changes).hasSize(1);
    }

    @Test
    void similarOptionals() {
        changes.addAll(ComponentDiff.compareOptionals(Optional.of("a"), Optional.of("a"), "foo"));

        assertThat(changes).hasSize(0);
    }

    @Test
    void differentOptionals() {
        changes.addAll(ComponentDiff.compareOptionals(Optional.of("a"), Optional.of("b"), "foo"));

        assertThat(changes).hasSize(1);
    }

    @Test
    void similarCollections() {
        changes.addAll(ComponentDiff.compareCollections(List.of("a", "b"), List.of("b", "a"), "foo"));
        assertThat(changes).hasSize(0);
    }

    @Test
    void differentCollectionEntries() {
        changes.addAll(ComponentDiff.compareCollections(List.of("a", "b"), List.of("c", "a"), "foo"));
        assertThat(changes).hasSize(1);
    }

    @Test
    void differentCollectionSize() {
        changes.addAll(ComponentDiff.compareCollections(List.of("a", "b"), List.of("a", "b", "c"), "foo"));
        assertThat(changes).hasSize(1);
    }

    @Test
    void differentCollectionSize2() {
        changes.addAll(ComponentDiff.compareCollections(List.of("a", "b", "c"), List.of("a", "b"), "foo"));
        assertThat(changes).hasSize(1);
    }
}