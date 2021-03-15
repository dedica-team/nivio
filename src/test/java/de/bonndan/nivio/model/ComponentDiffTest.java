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
        ComponentDiff.compareStrings(null, null, "foo", changes);
        assertThat(changes).hasSize(0);
    }

    @Test
    void twoNull() {
        ComponentDiff.compareStrings("a", null, "foo", changes);
        assertThat(changes).hasSize(1);
    }

    @Test
    void oneNull() {
        ComponentDiff.compareStrings(null, "b", "foo", changes);
        assertThat(changes).hasSize(1);
    }

    @Test
    void similarString() {
        ComponentDiff.compareStrings("b", "b", "foo", changes);
        assertThat(changes).hasSize(0);
    }

    @Test
    void differentString() {
        ComponentDiff.compareStrings("a", "b", "foo", changes);
        assertThat(changes).hasSize(1);
    }

    @Test
    void bothEmpty() {
        ComponentDiff.compareOptionals(Optional.empty(), Optional.empty(), "foo", changes);
        assertThat(changes).hasSize(0);
    }

    @Test
    void oneEmpty() {
        ComponentDiff.compareOptionals(Optional.empty(), Optional.of("a"), "foo", changes);
        assertThat(changes).hasSize(1);
    }

    @Test
    void twoEmpty() {
        ComponentDiff.compareOptionals(Optional.of("a"), Optional.empty(), "foo", changes);
        assertThat(changes).hasSize(1);
    }

    @Test
    void similarOptionals() {
        ComponentDiff.compareOptionals(Optional.of("a"), Optional.of("a"), "foo", changes);
        assertThat(changes).hasSize(0);
    }

    @Test
    void differentOptionals() {
        ComponentDiff.compareOptionals(Optional.of("a"), Optional.of("b"), "foo", changes);
        assertThat(changes).hasSize(1);
    }

    @Test
    void similarCollections() {
        ComponentDiff.compareCollections(List.of("a", "b"), List.of("b", "a"), "foo", changes);
        assertThat(changes).hasSize(0);
    }

    @Test
    void differentCollectionEntries() {
        ComponentDiff.compareCollections(List.of("a", "b"), List.of("c", "a"), "foo", changes);
        assertThat(changes).hasSize(1);
    }

    @Test
    void differentCollectionSize() {
        ComponentDiff.compareCollections(List.of("a", "b"), List.of("a", "b", "c"), "foo", changes);
        assertThat(changes).hasSize(1);
    }

    @Test
    void differentCollectionSize2() {
        ComponentDiff.compareCollections(List.of("a", "b", "c"), List.of("a", "b"), "foo", changes);
        assertThat(changes).hasSize(1);
    }
}