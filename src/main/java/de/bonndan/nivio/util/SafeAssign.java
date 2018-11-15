package de.bonndan.nivio.util;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SafeAssign {

    public static void assignSafe(String s, Consumer<String> c) {
        if (s != null) c.accept(s);
    }

    public static <T> void assignSafe(Set<T> s, Consumer<Set<T>> c) {
        if (s != null) c.accept(s);
    }

    public static <T> void assignSafe(List<T> s, Consumer<List<T>> c) {
        if (s != null) c.accept(s);
    }
}
