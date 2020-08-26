package de.bonndan.nivio.util;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class URLHelperTest {

	@Test
	void test_getURL() {
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
	void test_getURL_relativePath() {
		Path currentRelativePath = Paths.get("");
		String root = currentRelativePath.toAbsolutePath().toString();
		if (SystemUtils.IS_OS_WINDOWS) {
			root = "/" + root.replace(File.separator, "/");
		}
		URL url = URLHelper.getURL("src/test/resources/example/example_templates.yml");
		assert url != null;
		assertEquals("file:" + root + "/src/test/resources/example/example_templates.yml", url.toString());
	}

	@Test
	void test_splitQuery() throws MalformedURLException {
		URL url = new URL("http://192.168.99.100:8080?namespace=default&groupLabel=release");
		Map<String, String> actual = URLHelper.splitQuery(url);

		assertEquals(2, actual.size());

		assertTrue(actual.containsKey("namespace"));
		assertTrue(actual.containsKey("groupLabel"));

		assertEquals("default", actual.get("namespace"));
		assertEquals("release", actual.get("groupLabel"));
	}

	@Test
	void test_splitQuery_withoutQuery() throws MalformedURLException {
		URL url = new URL("http://192.168.99.100:8080");
		Map<String, String> actual = URLHelper.splitQuery(url);
		assertEquals(0, actual.size());
	}

	@Test
	void test_splitQuery_badQuery() throws MalformedURLException {
		URL url = new URL("http://192.168.99.100:8080?foo=");
		Map<String, String> actual = URLHelper.splitQuery(url);
		assertEquals(0, actual.size());
	}
}