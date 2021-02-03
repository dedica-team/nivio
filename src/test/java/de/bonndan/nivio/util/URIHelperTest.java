package de.bonndan.nivio.util;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class URIHelperTest {

    @Test
    void isURI() {
        Optional<URI> uri = URIHelper.getURI("mongodb://foo:somePw123@server.com:27017/adatabase");
        assertThat(uri).isPresent();
    }

    @Test
    void isNoURI() {
        Optional<URI> uri = URIHelper.getURI("...");
        assertThat(uri).isEmpty();
    }

    @Test
    void isEmptyWithoutHost() {
        Optional<URI> uri = URIHelper.getURI("mongodb://");
        assertThat(uri).isEmpty();
    }

    @Test
    void isEmptyWithoutScheme() {
        Optional<URI> uri = URIHelper.getURI("foobar");
        assertThat(uri).isEmpty();
    }
}