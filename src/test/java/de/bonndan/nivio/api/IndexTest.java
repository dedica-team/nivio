package de.bonndan.nivio.api;

import de.bonndan.nivio.model.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class IndexTest {

    private ConfigApiModel config;
    private final Index index = new Index(config);


    @BeforeEach
    public void setup() {
        config = mock(ConfigApiModel.class);
    }


    @Test
    void getOauth2Links() {
        // given
        String linkKey = "link1";
        Link link = new Link();
        Map<String, Link> oauth2links = Map.of(linkKey, link);

        // when
        index.getOauth2Links().put(linkKey, link);

        // then
        assertThat(index.getOauth2Links()).isEqualTo(oauth2links);
    }
}
