package de.bonndan.nivio.model;

import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LabelTest {

    /**
     * Exports the labels for GUI and docs
     * @throws IOException
     * @todo replace frontend export with DTO integration (#137)
     */
    @Test
    public void export() throws IOException {
        Map<String, String> labelExport = Label.export(false);

        File labelsJson = new File(RootPath.get() + "/src/main/app/src/labels.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(labelsJson, labelExport);

        File labelsRst = new File(RootPath.get() + "/docs/source/labels.rst");
        String s = labelExport.entrySet().stream()
                .map(entry -> "* **" + entry.getKey() + (Label.valueOf(entry.getKey()).isPrefix ? " (prefix)" : "") + "** " + entry.getValue())
                .collect(Collectors.joining("\n"));
        Files.writeString(labelsRst.toPath(), s, Charset.defaultCharset());
    }

}