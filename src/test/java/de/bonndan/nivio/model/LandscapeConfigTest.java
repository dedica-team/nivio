package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.kpi.KPIConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LandscapeConfigTest {

    private LandscapeConfig existing;

    @BeforeEach
    void setUp() {
        existing = new LandscapeConfig();
        createConfig(existing, true, 1, "one");
    }


    @Test
    void mergeEmpty() {

        //given
        LandscapeConfig emptyUpdate = new LandscapeConfig();

        //when
        LandscapeConfig merged = existing.merge(emptyUpdate);

        //then
        assertThat(merged.getKPIs()).isEqualTo(existing.getKPIs());
        assertThat(merged.getBranding().getMapStylesheet()).isEqualTo(existing.getBranding().getMapStylesheet());
        assertThat(merged.getLayoutConfig().getGroupMinDistanceLimit()).isEqualTo(existing.getLayoutConfig().getGroupMinDistanceLimit());
        assertThat(merged.getGroupBlacklist()).hasSameElementsAs(existing.getGroupBlacklist());
        assertThat(merged.getLabelBlacklist()).hasSameElementsAs(existing.getLabelBlacklist());
    }

    @Test
    void mergeValues() {

        //given
        LandscapeConfig config = new LandscapeConfig();
        createConfig(config, false, 2, "two");

        //when
        LandscapeConfig merged = existing.merge(config);

        //then
        assertThat(merged.getKPIs())
                .containsKey("two")
                .containsKey("one");

        assertThat(merged.isGreedy()).isFalse();
        assertThat(merged.getBranding().getMapStylesheet()).isEqualTo(config.getBranding().getMapStylesheet());

        assertThat(merged.getGroupBlacklist()).hasSameElementsAs(config.getGroupBlacklist());
        assertThat(merged.getLabelBlacklist()).hasSameElementsAs(config.getLabelBlacklist());

        assertThat(merged.getLayoutConfig().getGroupMinDistanceLimit()).isEqualTo(config.getLayoutConfig().getGroupMinDistanceLimit());
        assertThat(merged.getLayoutConfig().getGroupMaxDistanceLimit()).isEqualTo(config.getLayoutConfig().getGroupMaxDistanceLimit());
        assertThat(merged.getLayoutConfig().getItemMinDistanceLimit()).isEqualTo(config.getLayoutConfig().getItemMinDistanceLimit());
        assertThat(merged.getLayoutConfig().getItemMaxDistanceLimit()).isEqualTo(config.getLayoutConfig().getItemMaxDistanceLimit());

    }

    private void createConfig(LandscapeConfig existing, boolean greedy, int val1, String val2) {
        existing.setGreedy(greedy);
        existing.getLayoutConfig().setGroupLayoutInitialTemp(val1);
        existing.getLayoutConfig().setItemLayoutInitialTemp(val1);
        existing.getLayoutConfig().setGroupMaxDistanceLimit(val1);
        existing.getLayoutConfig().setGroupMinDistanceLimit(val1);
        existing.getLayoutConfig().setItemMaxDistanceLimit(val1);
        existing.getLayoutConfig().setItemMinDistanceLimit(val1);
        existing.getGroupBlacklist().add(val2);
        existing.getLabelBlacklist().add(val2);
        existing.getKPIs().put(val2, new KPIConfig());
    }
}