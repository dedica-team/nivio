package de.bonndan.nivio.util;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class URIHelperTest {

    @Test
    void empty() {
        assertThat(URIHelper.getURI("")).isEmpty();
        assertThat(URIHelper.getURI(null)).isEmpty();
    }

    @Test
    void isURI() {
        Optional<URI> uri = URIHelper.getURIWithHostAndScheme("mongodb://foo:somePw123@server.com:27017/adatabase");
        assertThat(uri).isPresent();
    }

    @Test
    void ipURI() {
        assertThat(URIHelper.getURI("192.168.100.123")).isPresent();
        assertThat(URIHelper.getURI("192.168.100.123/foo")).isPresent();
    }

    @Test
    void hostURI() {
        Optional<URI> uri = URIHelper.getURI("server.com");
        assertThat(uri).isPresent();
    }

    @Test
    void isNoURI() {
        Optional<URI> uri = URIHelper.getURIWithHostAndScheme("...");
        assertThat(uri).isEmpty();
    }

    @Test
    void isEmptyWithoutHost() {
        Optional<URI> uri = URIHelper.getURIWithHostAndScheme("mongodb://");
        assertThat(uri).isEmpty();
    }

    @Test
    void isEmptyWithoutScheme() {
        Optional<URI> uri = URIHelper.getURIWithHostAndScheme("foobar");
        assertThat(uri).isEmpty();
    }
}