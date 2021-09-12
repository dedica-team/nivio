package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdentifierValidationTest {

    @Test
    void assertValid() {
        IdentifierValidation.assertValid("1");
        IdentifierValidation.assertValid("22");
        IdentifierValidation.assertValid("foo");
        IdentifierValidation.assertValid("foo245");
        IdentifierValidation.assertValid("234foo");
        IdentifierValidation.assertValid("234_foo.bar:baz");
    }

    @Test
    void assertInvalid() {
        assertThrows(IllegalArgumentException.class, () -> IdentifierValidation.assertValid(null));
        assertThrows(IllegalArgumentException.class, () -> IdentifierValidation.assertValid(""));
        assertThrows(IllegalArgumentException.class, () -> IdentifierValidation.assertValid("1/1"));

        byte[] array = new byte[257]; // length is bounded by 7
        new Random().nextBytes(array);
        assertThrows(IllegalArgumentException.class, () -> IdentifierValidation.assertValid(new String(array, StandardCharsets.UTF_8)));
    }

    @Test
    void getValidId() {
        String foo = IdentifierValidation.getValidIdentifier("foo");
        assertThat(foo).isEqualTo("foo");

        foo = IdentifierValidation.getValidIdentifier(" foo ");
        assertThat(foo).isEqualTo("foo");

    }

    @Test
    void getValidIdValidates() {
        assertThrows(IllegalArgumentException.class, () -> IdentifierValidation.getValidIdentifier(""));
    }
}