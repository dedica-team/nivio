package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.*;
import joptsimple.internal.Strings;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Identifies a landscape {@link Component}.
 *
 * Resembles a slash-separated path beginning with landscape name, then group, then item.
 */
public class FullyQualifiedIdentifier {

    public static final String UNDEFINED = "_";

    public static final String SEPARATOR = "/";

    private FullyQualifiedIdentifier() {
    }

    /**
     * Creates a fqi for a DTO.
     *
     * @param dtoClass  dto class
     * @param landscape identifier
     * @param unit      unit
     * @param context   item / context
     * @param group     part / null
     * @return uri with placeholders if needed
     */
    public static URI forDescription(@NonNull final Class<? extends ComponentDescription> dtoClass,
                                     @Nullable final String landscape,
                                     @Nullable final String unit,
                                     @Nullable final String context,
                                     @Nullable final String group,
                                     @Nullable final String item,
                                     @Nullable final String part
    ) {
        return build(ComponentClass.getComponentClass(dtoClass),
                validOrSubstitute(landscape),
                validOrSubstitute(unit),
                validOrSubstitute(context),
                validOrSubstitute(group),
                validOrSubstitute(item),
                validOrSubstitute(part)
        );
    }

    private static String validOrSubstitute(String identifier) {
        return IdentifierValidation.getIdentifier(identifier).orElse(UNDEFINED);
    }

    /**
     * Creates a URI for a linked component.
     *
     * @param cls       scheme
     * @param landscape landscape
     * @param other     rest
     * @return a fqi based on the parts
     */
    public static URI build(@NonNull final Class<? extends GraphComponent> cls,
                            @NonNull final String landscape,
                            String... other
    ) {
        String rest = other == null ? "" : SEPARATOR + join(new ArrayList<>(Arrays.asList(other)));
        try {
            return new URI(cls.getSimpleName().toLowerCase(Locale.ROOT), Objects.requireNonNull(landscape), rest, null, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Cannot create uri from %s %s %s", cls, landscape, Arrays.toString(other)));
        }
    }

    private static String join(List<String> other) {
        for (int i = other.size() - 1; i >= 0; i--) {
            String s = other.get(i);
            if (isUndefined(s)) {
                other.remove(i);
            } else {
                break;
            }
        }

        return Strings.join(other.stream().filter(StringUtils::hasLength).collect(Collectors.toList()), SEPARATOR);
    }

    @NonNull
    static URI from(@Nullable final URI parent, GraphComponent child) {
        if (!StringUtils.hasLength(child.identifier)) {
            throw new IllegalArgumentException("Identifier must not be empty.");
        }

        Class<GraphComponent> aClass = (Class<GraphComponent>) child.getClass();
        if (parent == null) {
            return build(aClass, child.getIdentifier());
        }

        return build(aClass, parent.getAuthority(), getPath(parent), child.getIdentifier());
    }

    public static String getPath(URI parent) {
        return StringUtils.trimLeadingCharacter(parent.getPath().toLowerCase(Locale.ROOT), SEPARATOR.charAt(0));
    }

    public static boolean isUndefined(String s) {
        return !StringUtils.hasLength(s) || UNDEFINED.equals(s);
    }
}
