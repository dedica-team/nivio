package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Optional;

public class IdentifierValidation {

    /**
     * A valid identifier must be of length greater than zero and start with an a-z character or number.
     */
    public static final String IDENTIFIER_PATTERN = "^[\\w][\\w._-]{0,255}$";

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

        if (!identifier.matches(IDENTIFIER_PATTERN)) {
            throw new IllegalArgumentException(String.format("Invalid  identifier given: '%s', it must match %s", identifier, IDENTIFIER_PATTERN));
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
        if (FullyQualifiedIdentifier.isUndefined(identifier)) {
            throw new IllegalArgumentException(String.format("Invalid identifier '%s'given.", identifier));
        }
        //noinspection ConstantConditions
        String trimmed = StringUtils.trimWhitespace(identifier);
        IdentifierValidation.assertValid(trimmed);
        return trimmed;
    }

    @NonNull
    public static Optional<String> getIdentifier(@Nullable final String identifier) {
        try {
            return Optional.of(getValidIdentifier(identifier));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
