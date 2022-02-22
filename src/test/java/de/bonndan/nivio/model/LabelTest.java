package de.bonndan.nivio.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static de.bonndan.nivio.model.Label.INTERNAL_LABEL_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

class LabelTest {

    /**
     * Exports the labels for GUI and docs
     */
    @Test
    void export() throws IOException {
        Map<String, String> labelExport1 = new LinkedHashMap<>();
        List<Label> sortedLabels = Arrays.stream(Label.values()).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
        sortedLabels.stream()
                .filter(label -> !label.isPrefix)
                .filter(label -> !label.name().startsWith(INTERNAL_LABEL_PREFIX))
                .forEach(label -> labelExport1.put(label.name(), label.meaning));

        File labelsJson = new File(RootPath.get() + "/src/main/app/src/labels.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(labelsJson, labelExport1);

        File labelsRst = new File(RootPath.get() + "/docs/source/inc_labels.rst");
        String s = labelExport1.entrySet().stream()
                .map(entry -> "* ``" + entry.getKey() + (Label.valueOf(entry.getKey()).isPrefix ? " (prefix)" : "") + "`` " + entry.getValue())
                .collect(Collectors.joining("\n"));
        Files.writeString(labelsRst.toPath(), s, Charset.defaultCharset());

        assertThat(labelsRst).exists();
    }

}
