package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecureLabelsProcessorTest {

    private ItemDescription item;
    private SecureLabelsProcessor secureLabelsProcessor;
    private LandscapeDescription landscapeDescription;

    @BeforeEach
    public void setup() {
        item = new ItemDescription();
        item.setIdentifier("foo");

        landscapeDescription = new LandscapeDescription("identifier", "name", null);
        landscapeDescription.addItems(List.of(item));

        secureLabelsProcessor = new SecureLabelsProcessor();
    }

    @Test
    public void regularLabels() {
        item.setLabel("foo", "bar");
        item.setLabel("niviofoo", "baz");

        secureLabelsProcessor.process(landscapeDescription);

        assertEquals("bar", item.getLabels().get("foo"));
        assertEquals("baz", item.getLabels().get("niviofoo"));
    }

    @Test
    public void fieldLabel() {
        item.setLabel("foo", "bar");
        item.setLabel("nivio.description", "baz");

        secureLabelsProcessor.process(landscapeDescription);

        assertEquals("bar", item.getLabels().get("foo"));
    }

    @Test
    public void pass() {
        item.setLabel("pass", "x");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals(SecureLabelsProcessor.MASK, item.getLabels().get("pass"));
    }

    @Test
    public void apass() {
        item.setLabel("a.pass_", "x");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals(SecureLabelsProcessor.MASK, item.getLabels().get("a.pass_"));
    }

    @Test
    public void secret() {
        item.setLabel("secret", "x");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals(SecureLabelsProcessor.MASK, item.getLabels().get("secret"));
    }

    @Test
    public void my_secret_x() {
        item.setLabel("my_secret_x", "x");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals(SecureLabelsProcessor.MASK, item.getLabels().get("my_secret_x"));
    }

    @Test
    public void credentials() {
        item.setLabel("credentials", "x");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals(SecureLabelsProcessor.MASK, item.getLabels().get("credentials"));
    }

    @Test
    public void ucredentials() {
        item.setLabel("_credentials_", "x");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals(SecureLabelsProcessor.MASK, item.getLabels().get("_credentials_"));
    }

    @Test
    public void token() {
        item.setLabel("token", "x");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals(SecureLabelsProcessor.MASK, item.getLabels().get("token"));
    }

    @Test
    public void atoken() {
        item.setLabel("a_token_", "x");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals(SecureLabelsProcessor.MASK, item.getLabels().get("a_token_"));
    }

    @Test
    public void key() {
        item.setLabel("key", "x");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals(SecureLabelsProcessor.MASK, item.getLabels().get("key"));
    }

    @Test
    public void akey() {
        item.setLabel("a_key_", "x");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals(SecureLabelsProcessor.MASK, item.getLabels().get("a_key_"));
    }

    @Test
    public void secretUrl() {
        item.setLabel("foo", "http://very:secret@foobar.com:8080/a/path?foo=bar");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals("http://*@foobar.com:8080/a/path?foo=bar", item.getLabels().get("foo"));
    }

    @Test
    public void simpleSecretUrl() {
        item.setLabel("foo", "http://very:secret@foobar.com");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals("http://*@foobar.com", item.getLabels().get("foo"));
    }


    @Test
    public void regularUrl() {
        item.setLabel("foo", "http://foobar.com");
        secureLabelsProcessor.process(landscapeDescription);
        assertEquals("http://foobar.com", item.getLabels().get("foo"));
    }

}