package de.bonndan.nivio.util;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class SafeAssign {

    /**
     * Assigns the value if not null else ""
     *
     * @param s value
     * @param c consumer
     */
    public static void assignValueOrEmpty(String s, Consumer<String> c) {
        c.accept(Objects.requireNonNullElse(s, ""));
    }

    /**
     * Assigns the value if not null
     *
     * @param s value
     * @param c consumer
     */
    public static void assignSafe(String s, Consumer<String> c) {
        if (s != null) c.accept(s);
    }

    /**
     * Passes the first arg to the consumer if it is not null and the second arg is null.
     *
     * @param s      value
     * @param absent null val
     * @param c      value consumer
     */
    public static void assignSafeIfAbsent(String s, String absent, Consumer<String> c) {
        if (s != null && absent == null) c.accept(s);
    }

    public static <T> void assignSafe(Set<T> s, Consumer<Set<T>> c) {
        if (s != null) c.accept(s);
    }

    public static <T> void assignSafe(List<T> s, Consumer<List<T>> c) {
        if (s != null) c.accept(s);
    }
}
