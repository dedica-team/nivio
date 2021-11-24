package de.bonndan.nivio.util;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class URLFactoryTest {

    @Test
    void test_getURL() {
        //given
        String root = Paths.get("").toAbsolutePath().toString();

        //when
        Optional<URL> url = URLFactory.getURL(root + "/src/test/resources/example/example_templates.yml");
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
        Optional<URL> url = URLFactory.getURL("src/test/resources/example/example_templates.yml");

        //then
        assertTrue(url.isPresent());
        assertEquals("file:" + root + "/src/test/resources/example/example_templates.yml", url.get().toString());
    }

    @Test
    void test_splitQuery() throws MalformedURLException {
        //given
        URL url = new URL("http://192.168.99.100:8080?namespace=default&groupLabel=release");


        //when
        Map<String, String> actual = splitQuery(url);

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
        Map<String, String> actual = splitQuery(url);
        //then
        assertEquals(0, actual.size());
    }

    @Test
    void test_splitQuery_badQuery() throws MalformedURLException {
        //given
        URL url = new URL("http://192.168.99.100:8080?foo=");
        //when
        Map<String, String> actual = splitQuery(url);
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
        String actual = URLFactory.combine(baseUrl, part);

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
        String actual = URLFactory.combine(baseUrl, part);

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
        String actual = URLFactory.combine(baseUrl, part);

        //then
        assertEquals(part, actual);
    }

    /**
     * https://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection,
     */
    private Map<String, String> splitQuery(@NonNull URL url) {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String query = url.getQuery();
        if (query == null) {
            return queryPairs;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx == -1 || idx + 1 > pair.length() - 1) {
                continue;
            }
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }
        return queryPairs;
    }

    @Test
    void withHttp() {
        Optional<URL> url = URLFactory.getURL("http://test.com");
        assertThat(url).isPresent().map(URL::toString).get().isEqualTo("http://test.com");
    }

    @Test
    void withNonExistingFileIsNull() {
        Optional<URL> url = URLFactory.getURL(new File(RootPath.get() + "/foo/bar.jpg").getAbsolutePath());
        assertThat(url).isEmpty();
    }

    @Test
    void withFile() {
        Optional<URL> url = URLFactory.getURL(new File(RootPath.get() + "/readme.md").getAbsolutePath());
        assertThat(url).isPresent();
        assertThat(url.get().toString()).contains("/readme.md");
    }

    @Test
    void partial() {
        Optional<URL> url = URLFactory.getURL("./foo/bar.jpg");
        assertThat(url).isNotNull();
        assertThat(url.toString()).contains(URLFactory.RELATIVE_PATH_PLACEHOLDER);
    }

    @Test
    void unrecoverable() {
        assertThat(URLFactory.getURL("")).isEmpty();
        assertThat(URLFactory.getURL("foobar$+")).isEmpty();
    }
}
