package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GroupApiModelTest {
    GroupApiModel groupApiModel;

    @BeforeEach
    void setUp() {
        var group = new Group("test", "test", "testOwner", "testDescription", "testContact", "testIcon", "testColor");
        groupApiModel = new GroupApiModel(group, new HashSet<>());
    }

    @Test
    void getFullyQualifiedIdentifier() {
        assertThat(groupApiModel.getFullyQualifiedIdentifier()).isEqualTo(FullyQualifiedIdentifier.build("test", "test", ""));
    }

    @Test
    void getName() {
        assertThat(groupApiModel.getName()).isEqualTo("test");
    }

    @Test
    void getIdentifier() {
        assertThat(groupApiModel.getIdentifier()).isEqualTo("test");
    }

    @Test
    void getOwner() {
        assertThat(groupApiModel.getOwner()).isEqualTo("testOwner");
    }

    @Test
    void getDescription() {
        assertThat(groupApiModel.getDescription()).isEqualTo("testDescription");
    }

    @Test
    void getContact() {
        assertThat(groupApiModel.getContact()).isEqualTo("testContact");
    }

    @Test
    void getColor() {
        assertThat(groupApiModel.getColor()).isEqualTo("ec0000");
    }

    @Test
    void getItems() {
        assertThat(groupApiModel.getItems()).isEqualTo(Set.of());
    }

    @Test
    void getLabels() {
        assertThat(groupApiModel.getLabels()).isEqualTo(Map.of());
    }

    @Test
    void getIcon() {
        assertThat(groupApiModel.getIcon()).isEqualTo("testIcon");
    }

    @Test
    void getLandscapeIdentifier() {
        assertThat(groupApiModel.getLandscapeIdentifier()).isEqualTo("test");
    }
}