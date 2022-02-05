package de.bonndan.nivio.input.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RelationDescriptionTest {

    private List<RelationDescription> relations;

    @BeforeEach
    void setup() {
        relations = List.of(new RelationDescription("agroup/foo", "bgroup/bar"));
    }

    @Test
    void validatesIdentifiers() {
        assertDoesNotThrow(() -> new RelationDescription("aGroup/foo", "bGroup/Bar"));
        assertDoesNotThrow(() -> new RelationDescription("1fe96e39-0b63-49c0-97b7-6b79c27618f1", "76e6f2fc-a344-4590-9ec9-b78cdedd6c50"));
        assertDoesNotThrow(() -> new RelationDescription("foo:bar.com", "foo:bar.com"));
        assertThrows(IllegalArgumentException.class, () -> new RelationDescription("", "foobar$"));
        assertThrows(IllegalArgumentException.class, () -> new RelationDescription("asdasd", ""));
    }

    @Test
    void findMatching() {
        var rel = new RelationDescription("aGroup/foo", "bGroup/Bar");

        //when
        Optional<RelationDescription> matching = rel.findMatching(relations);

        //then
        assertThat(matching).isPresent();
    }

    @Test
    void findMatchingOtherSource() {
        var rel = new RelationDescription("aGroup/notfoo", "bGroup/Bar");

        //when
        Optional<RelationDescription> matching = rel.findMatching(relations);

        //then
        assertThat(matching).isEmpty();
    }

    @Test
    void findMatchingOtherTarget() {
        var rel = new RelationDescription("aGroup/foo", "bGroup/notBar");

        //when
        Optional<RelationDescription> matching = rel.findMatching(relations);

        //then
        assertThat(matching).isEmpty();
    }

    @Test
    void update() {
        var rel = new RelationDescription("aGroup/foo", "bGroup/notBar");
        rel.setLabel("foo", "one");
        rel.setFormat("f1");

        var newer = new RelationDescription("aGroup/foo", "bGroup/notBar");
        newer.setDescription("one");
        newer.setFormat("foo");
        newer.setLabel("foo", "two");

        //when
        rel.update(newer);

        //then
        assertThat(rel.getDescription()).isEqualTo("one");
        assertThat(rel.getFormat()).isEqualTo("foo");
        assertThat(rel.getLabel("foo")).isEqualTo("two");
    }

}
