package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.Source;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LandscapeApiModelTest {
    LandscapeApiModel landscapeApiModel;
    Source source;
    LandscapeConfig landscapeConfig;
    ProcessLog processLog;

    @BeforeEach
    void setUp() {
        source = Mockito.mock(Source.class);
        landscapeConfig = Mockito.mock(LandscapeConfig.class);
        processLog = Mockito.mock(ProcessLog.class);
        var landscape = new Landscape("test", Map.of(), "testName", "testContact", "testOwner", "testDescription", source, landscapeConfig, processLog, Map.of());
        landscape.setLabel(Label.icon, "icon");
        landscape.setLabel(Label._icondata, "icon,base64");
        landscapeApiModel = new LandscapeApiModel(landscape);
    }

    @Test
    void getFullyQualifiedIdentifier() {
        assertThat(landscapeApiModel.getFullyQualifiedIdentifier()).isEqualTo(FullyQualifiedIdentifier.build("test", "", ""));
    }

    @Test
    void getName() {
        assertThat(landscapeApiModel.getName()).isEqualTo("testName");
    }

    @Test
    void getIdentifier() {
        assertThat(landscapeApiModel.getIdentifier()).isEqualTo("test");
    }

    @Test
    void getOwner() {
        assertThat(landscapeApiModel.getOwner()).isEqualTo("testOwner");
    }

    @Test
    void getDescription() {
        assertThat(landscapeApiModel.getDescription()).isEqualTo("testDescription");
    }

    @Test
    void getContact() {
        assertThat(landscapeApiModel.getContact()).isEqualTo("testContact");
    }

    @Test
    void getLabels() {
        assertThat(landscapeApiModel.getLabels()).isEqualTo(Map.of("icon", "icon"));
    }

    @Test
    void getIcon() {
        assertThat(landscapeApiModel.getIcon()).isEqualTo("icon,base64");
    }

    @Test
    void getConfig() {
        assertThat(landscapeApiModel.getConfig()).isEqualTo(landscapeConfig);
    }

    @Test
    void getGroups() {
        assertThat(landscapeApiModel.getGroups()).isEqualTo(Set.of());
    }

    @Test
    void getLastUpdate() {
        assertThat(landscapeApiModel.getLastUpdate()).isNull();
    }

    @Test
    void getKpis() {
        assertThat(landscapeApiModel.getKpis()).isEqualTo(Map.of());
    }
}