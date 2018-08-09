package de.bonndan.nivio.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RootPath {
    public static String get() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }

    public static String getResources() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString() + "/src/main/resources";
    }
}
