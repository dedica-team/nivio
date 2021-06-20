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

}