package de.bonndan.nivio.output.icons;

import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DataUrlHelperTest {

    @Test
    void doesNotReloadDataUrls() {
        String path = DataUrlHelper.DATA_IMAGE + "anything";
        Optional<String> s = DataUrlHelper.asBase64(path);
        assertThat(s).isPresent();
        assertThat(s.get()).isEqualTo(path);
    }
}