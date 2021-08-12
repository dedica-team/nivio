package de.bonndan.nivio.output.icons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalIconsTest {

    private LocalIcons localIcons;

    @BeforeEach
    public void setup() {
        localIcons = new LocalIcons();
    }

    @Test
    void throwsIfIconsMissing() {
        assertThrows(RuntimeException.class, () -> new LocalIcons(System.getProperty("java.io.tmpdir")));
    }

    @Test
    void encodesBase64DataUrls() {
        Optional<String> icon = localIcons.getIconUrl(IconMapping.DEFAULT_ICON);
        assertThat(icon).isNotEmpty();

        String payload = icon.get().replace(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64, "");
        String decoded = new String(Base64.getDecoder().decode(payload));
        assertThat(decoded).contains("xml");
    }

    @Test
    void returnsDefault() {
        String icon = localIcons.getDefaultIcon();
        assertThat(icon).isNotEmpty();

        String payload = icon.replace(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64, "");
        String decoded = new String(Base64.getDecoder().decode(payload));
        assertThat(decoded).contains("xml");
    }

    @Test
    void returnsTypeIgnoreCase() {
        assertThat(localIcons.getIconUrl("AccOunT")).isNotEmpty();
    }
}