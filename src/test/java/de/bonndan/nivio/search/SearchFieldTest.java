package de.bonndan.nivio.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SearchFieldTest {

    /**
     * Exports the search fields for GUI and docs
     */
    @Test
    void export() throws IOException {
        Map<String, String> export = new LinkedHashMap<>();
        Arrays.stream(SearchField.values())
                .sorted(Comparator.comparing(Enum::name))
                .collect(Collectors.toList())
                .stream()
                .filter(SearchField::isPublic)
                .forEach(searchField -> export.put(searchField.getValue(), searchField.getDescription()));

        File json = new File(RootPath.get() + "/src/main/app/src/searchFields.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(json, export);

        File rstFile = new File(RootPath.get() + "/docs/source/inc_searchFields.rst");
        String s = export.entrySet().stream()
                .map(entry -> "* ``" + entry.getKey() + "`` " + entry.getValue())
                .collect(Collectors.joining("\n"));
        Files.writeString(rstFile.toPath(), s, Charset.defaultCharset());

        assertThat(rstFile).exists();
    }
}