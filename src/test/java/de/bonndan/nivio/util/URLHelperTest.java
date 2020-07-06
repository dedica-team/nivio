package de.bonndan.nivio.util;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class URLHelperTest {

    @Test
    public void test_getURL() {
        Path currentRelativePath = Paths.get("");
        String root = currentRelativePath.toAbsolutePath().toString();
        URL url = URLHelper.getURL(root + "/src/test/resources/example/example_templates.yml");
        if (SystemUtils.IS_OS_WINDOWS) {
            root = "/" + root.replace(File.separator, "/");
        }
        assert url != null;
        assertEquals("file:" + root + "/src/test/resources/example/example_templates.yml", url.toString());
    }

    @Test
    public void test_getURL_relativePath() {
        Path currentRelativePath = Paths.get("");
        String root = currentRelativePath.toAbsolutePath().toString();
        if (SystemUtils.IS_OS_WINDOWS) {
            root = "/" + root.replace(File.separator, "/");
        }
        URL url = URLHelper.getURL("src/test/resources/example/example_templates.yml");
        assert url != null;
        assertEquals("file:" + root + "/src/test/resources/example/example_templates.yml", url.toString());
    }
}