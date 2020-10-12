package de.bonndan.nivio.util;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class URLHelperTest {

    @Test
    void test_getURL() {
        //given
        String root = Paths.get("").toAbsolutePath().toString();

        //when
        Optional<URL> url = URLHelper.getURL(root + "/src/test/resources/example/example_templates.yml");
        if (SystemUtils.IS_OS_WINDOWS) {
            root = "/" + root.replace(File.separator, "/");
        }

        //then
        assertTrue(url.isPresent());
        assertEquals("file:" + root + "/src/test/resources/example/example_templates.yml", url.get().toString());
    }

    @Test
    void test_getURL_relativePath() {
        //given
        String root = Paths.get("").toAbsolutePath().toString();
        if (SystemUtils.IS_OS_WINDOWS) {
            root = "/" + root.replace(File.separator, "/");
        }
        //when
        Optional<URL> url = URLHelper.getURL("src/test/resources/example/example_templates.yml");

        //then
        assertTrue(url.isPresent());
        assertEquals("file:" + root + "/src/test/resources/example/example_templates.yml", url.get().toString());
    }

    @Test
    void test_splitQuery() throws MalformedURLException {
        //given
        URL url = new URL("http://192.168.99.100:8080?namespace=default&groupLabel=release");


        //when
        Map<String, String> actual = URLHelper.splitQuery(url);

        //then
        assertEquals(2, actual.size());

        assertTrue(actual.containsKey("namespace"));
        assertTrue(actual.containsKey("groupLabel"));

        assertEquals("default", actual.get("namespace"));
        assertEquals("release", actual.get("groupLabel"));
    }

    @Test
    void test_splitQuery_withoutQuery() throws MalformedURLException {
        //given
        URL url = new URL("http://192.168.99.100:8080");
        //when
        Map<String, String> actual = URLHelper.splitQuery(url);
        //then
        assertEquals(0, actual.size());
    }

    @Test
    void test_splitQuery_badQuery() throws MalformedURLException {
        //given
        URL url = new URL("http://192.168.99.100:8080?foo=");
        //when
        Map<String, String> actual = URLHelper.splitQuery(url);
        //then
        assertEquals(0, actual.size());
    }


    @Test
    void test_combine() throws MalformedURLException {
        //given
        String root = Paths.get("").toAbsolutePath().toString();
        if (SystemUtils.IS_OS_WINDOWS) {
            root = "/" + root.replace(File.separator, "/");
        }
        URL baseUrl = new URL("file:" + root + "/src/test/resources/example/");
        String part = "./services/wordpress.yml";

        //when
        String actual = URLHelper.combine(baseUrl, part);

        //then
        String expected = "file:" + root + "/src/test/resources/example/services/wordpress.yml";

        assertEquals(expected, actual);
    }

    @Test
    void test_combine_absolute_part() throws MalformedURLException {
        //given
        String root = Paths.get("").toAbsolutePath().toString();
        if (SystemUtils.IS_OS_WINDOWS) {
            root = "/" + root.replace(File.separator, "/");
        }
        URL baseUrl = new URL("file:" + root + "/src/test/resources/example/");
        String part = "http://192.168.99.100:8080";

        //when
        String actual = URLHelper.combine(baseUrl, part);

        //then
        String expected = "http://192.168.99.100:8080";

        assertEquals(expected, actual);
    }

    @Test
    void test_combine_absolute_part2() throws MalformedURLException {
        //given
        String root = Paths.get("").toAbsolutePath().toString();
        if (SystemUtils.IS_OS_WINDOWS) {
            root = "/" + root.replace(File.separator, "/");
        }
        URL baseUrl = new URL("file:" + root + "/src/test/resources/example/");
        String part = "file:" + root + "/src/test/resources/example/example_templates.yml";

        //when
        String actual = URLHelper.combine(baseUrl, part);

        //then
        assertEquals(part, actual);
    }
}
