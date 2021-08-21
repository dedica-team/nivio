package de.bonndan.nivio.output.icons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class IconMappingTest {

    private IconMapping iconMapping;

    @BeforeEach
    void setup() {
        iconMapping = new IconMapping();
    }

    @Test
    void loadsNames() {
        String name = "gift";
        Optional<String> icon = iconMapping.getIcon(name);
        assertThat(icon).isPresent();
    }

    @Test
    void loadsAliases() {
        String alias = "present";
        Optional<String> icon = iconMapping.getIcon(alias);
        assertThat(icon).isPresent();
        assertThat(icon.get()).isEqualTo("gift");
    }

    @Test
    void fails() {
        String name = "foobarbaz";
        Optional<String> icon = iconMapping.getIcon(name);
        assertThat(icon).isEmpty();
    }
}