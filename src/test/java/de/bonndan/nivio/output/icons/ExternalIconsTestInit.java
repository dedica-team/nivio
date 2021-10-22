package de.bonndan.nivio.output.icons;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
class ExternalIconsInitTest {
    @Autowired
    ExternalIconsProvider externalIconsProvider;
    @Autowired
    ExternalIcons externalIconsTest;

    @Test
    void testInit() {
        for (Map.Entry<String, String> entry : externalIconsProvider.getUrls().entrySet()) {
            assertThat(externalIconsTest.getUrl(entry.getKey())).isPresent();
        }
    }
}
