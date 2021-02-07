package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SecureLabelsResolverTest {

    private ItemDescription item;
    private SecureLabelsResolver secureLabelsResolver;
    private LandscapeDescription landscapeDescription;

    @BeforeEach
    public void setup() {
        item = new ItemDescription();
        item.setIdentifier("foo");

        landscapeDescription = new LandscapeDescription("foo");
        landscapeDescription.addItems(List.of(item));

        secureLabelsResolver = new SecureLabelsResolver(new ProcessLog(mock(Logger.class)));
    }

    @Test
    public void regularLabels() {
        item.setLabel("foo", "bar");
        item.setLabel("niviofoo", "baz");

        secureLabelsResolver.resolve(landscapeDescription);

        assertEquals("bar", item.getLabels().get("foo"));
        assertEquals("baz", item.getLabels().get("niviofoo"));
    }

    @Test
    public void fieldLabel() {
        item.setLabel("foo", "bar");
        item.setLabel("nivio.description", "baz");

        secureLabelsResolver.resolve(landscapeDescription);

        assertEquals("bar", item.getLabels().get("foo"));
    }

    @Test
    public void pass() {
        item.setLabel("pass", "x");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals(SecureLabelsResolver.MASK, item.getLabels().get("pass"));
    }

    @Test
    public void apass() {
        item.setLabel("a.pass_", "x");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals(SecureLabelsResolver.MASK, item.getLabels().get("a.pass_"));
    }

    @Test
    public void secret() {
        item.setLabel("secret", "x");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals(SecureLabelsResolver.MASK, item.getLabels().get("secret"));
    }

    @Test
    public void my_secret_x() {
        item.setLabel("my_secret_x", "x");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals(SecureLabelsResolver.MASK, item.getLabels().get("my_secret_x"));
    }

    @Test
    public void credentials() {
        item.setLabel("credentials", "x");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals(SecureLabelsResolver.MASK, item.getLabels().get("credentials"));
    }

    @Test
    public void ucredentials() {
        item.setLabel("_credentials_", "x");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals(SecureLabelsResolver.MASK, item.getLabels().get("_credentials_"));
    }

    @Test
    public void token() {
        item.setLabel("token", "x");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals(SecureLabelsResolver.MASK, item.getLabels().get("token"));
    }

    @Test
    public void atoken() {
        item.setLabel("a_token_", "x");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals(SecureLabelsResolver.MASK, item.getLabels().get("a_token_"));
    }

    @Test
    public void key() {
        item.setLabel("key", "x");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals(SecureLabelsResolver.MASK, item.getLabels().get("key"));
    }

    @Test
    public void akey() {
        item.setLabel("a_key_", "x");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals(SecureLabelsResolver.MASK, item.getLabels().get("a_key_"));
    }

    @Test
    public void secretUrl() {
        item.setLabel("foo", "http://very:secret@foobar.com:8080/a/path?foo=bar");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals("http://*@foobar.com:8080/a/path?foo=bar", item.getLabels().get("foo"));
    }

    @Test
    public void simpleSecretUrl() {
        item.setLabel("foo", "http://very:secret@foobar.com");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals("http://*@foobar.com", item.getLabels().get("foo"));
    }


    @Test
    public void regularUrl() {
        item.setLabel("foo", "http://foobar.com");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals("http://foobar.com", item.getLabels().get("foo"));
    }

    @Test
    public void dsn() {
        item.setLabel("mongodb_dsn", "mongodb://foo:somePw123@server.com:27017/adatabase");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals("mongodb://*@server.com:27017/adatabase", item.getLabels().get("mongodb_dsn"));
    }

    @Test
    public void dsnWithoutSecret() {
        item.setLabel("mongodb_dsn", "mongodb://server.com:27017/adatabase");
        secureLabelsResolver.resolve(landscapeDescription);
        assertEquals("mongodb://server.com:27017/adatabase", item.getLabels().get("mongodb_dsn"));
    }
}