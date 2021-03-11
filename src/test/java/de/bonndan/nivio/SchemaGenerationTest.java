package de.bonndan.nivio;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.output.icons.IconService;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.ObjectMapperFactory;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class SchemaGenerationTest {


    private ModelConverters converters;

    @BeforeEach
    void setup() {
        converters = ModelConverters.getInstance();

    }

    @Test
    void generateDocs() {
        Components components = new Components();
        Map<String, Schema> schema = converters.readAll(new AnnotatedType(LandscapeDescription.class));
        components.setSchemas(schema);
        OpenAPI openAPI = new OpenAPI();
        openAPI.setComponents(components);
        openAPI.setInfo(new Info());
        openAPI.getInfo().setTitle("Nivio Input Models");
        openAPI.getInfo().setDescription("The models used to generated landscapes and their components.");
            try {
                String s1 = Json.pretty(openAPI);
                Files.write(new File(String.format("docs/source/schema/%s.json", "spec")).toPath(), s1.getBytes(StandardCharsets.UTF_8));

            } catch (IOException e) {
                throw new RuntimeException("Failed to generate model specs.", e);
            }
    }
}
