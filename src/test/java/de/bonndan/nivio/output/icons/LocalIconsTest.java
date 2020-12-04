package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.model.Item;
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
    public void throwsIfIconsMissing() {
        assertThrows(RuntimeException.class, () -> new LocalIcons(System.getProperty("java.io.tmpdir")));
    }

    @Test
    public void encodesBase64DataUrls() {
        Optional<String> icon = localIcons.getIconUrl(IconMapping.DEFAULT_ICON.getIcon());
        assertThat(icon).isNotEmpty();

        String payload = icon.get().replace(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64, "");
        String decoded = new String(Base64.getDecoder().decode(payload));
        assertThat(decoded).contains("xml");
    }

    @Test
    public void returnsDefault() {
        String icon = localIcons.getDefaultIcon();
        assertThat(icon).isNotEmpty();

        String payload = icon.replace(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64, "");
        String decoded = new String(Base64.getDecoder().decode(payload));
        assertThat(decoded).contains("xml");
    }

    @Test
    public void returnsTypeIgnoreCase() {
        Item item = new Item("test", "a");
        item.setType("");

        assertThat(localIcons.getIconUrl("AccOunT")).isNotEmpty();
    }
}