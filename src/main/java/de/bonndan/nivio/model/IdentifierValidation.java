package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class IdentifierValidation {

    /**
     * A valid identifier must be of length greater than zero and start with an a-z character or number.
     */
    public static final String PATTERN = "^[\\w][\\w.:_-]{0,255}$";

    private IdentifierValidation() {
    }

    /**
     * Asserts that the given identifier matches the required pattern.
     *
     * @param identifier component identifier
     * @throws IllegalArgumentException if the identifier is invalid
     */
    public static void assertValid(@Nullable final String identifier) {
        if (!StringUtils.hasLength(identifier)) {
            throw new IllegalArgumentException("Invalid empty identifier given.");
        }

        if (!identifier.matches(PATTERN)) {
            throw new IllegalArgumentException(String.format("Invalid  identifier given: '%s', it must match %s", identifier, PATTERN));
        }
    }

    /**
     * Validates and trims the given identifier.
     *
     * @param identifier component identifier
     * @return trimmed string
     * @throws IllegalArgumentException if the identifier is invalid
     */
    @NonNull
    public static String getValidIdentifier(@Nullable final String identifier) {
        if (!StringUtils.hasLength(identifier)) {
            throw new IllegalArgumentException("Invalid empty identifier given.");
        }
        //noinspection ConstantConditions
        String trimmed = StringUtils.trimWhitespace(identifier);
        IdentifierValidation.assertValid(trimmed);
        return trimmed;
    }
}
