package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.model.ContextBuilder;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.GroupBuilder;
import de.bonndan.nivio.model.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GroupApiModelTest {
    private GroupApiModel groupApiModel;
    private Group group;

    @BeforeEach
    void setUp() {
        group = GroupBuilder.aGroup()
                .withIdentifier("test")
                .withName("test")
                .withOwner("testOwner")
                .withDescription("testDescription")
                .withContact("testContact")
                .withColor("testColor")
                .withParent(ContextBuilder.aTestContext("default").build())
                .build();

        groupApiModel = new GroupApiModel(group, new HashSet<>());
        group.setLabel(Label._icondata, "iconurl,base64");
    }

    @Test
    void getFullyQualifiedIdentifier() {
        assertThat(groupApiModel.getFullyQualifiedIdentifier()).isEqualTo(group.getFullyQualifiedIdentifier());
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
        assertThat(groupApiModel.getIcon()).isEqualTo("iconurl,base64");
    }
}