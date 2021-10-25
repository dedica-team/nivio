package de.bonndan.nivio.output.icons;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
class ExternIconYamlTest {
    @Autowired
    ExternalIconsProvider externalIconsProvider;
    @Autowired
    ExternalIcons externalIconsTest;

    @Test
    void init() {
        var externalIconsUrl = externalIconsProvider.getUrls().entrySet();
        for (Map.Entry<String, String> entry : externalIconsUrl) {
            Optional<String> url = externalIconsTest.getUrl(entry.getKey());
            assertThat(url).withFailMessage("URL for " + entry.getKey() + " is empty.").isPresent();
        }
    }
}