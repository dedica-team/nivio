package de.bonndan.nivio;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.languages.OpenAPIGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;

public class SchemaGenerationTest {

    @Test
    void generateDocs() {
        Components components = new Components();
        components.setSchemas(getSchemaMap());
        OpenAPI openAPI = new OpenAPI();
        openAPI.setComponents(components);
        openAPI.setPaths(new Paths());
        openAPI.setInfo(new Info());
        openAPI.getInfo().setTitle("Nivio Input Models");
        openAPI.getInfo().setDescription("The models used to generated landscapes and their components.");
        try {
            String s1 = Json.pretty(openAPI);
            Files.write(new File(String.format("docs/source/schema/%s.json", "spec")).toPath(), s1.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate model specs.", e);
        }

        OpenAPIGenerator openAPIGenerator = new OpenAPIGenerator();
        openAPIGenerator.setOpenAPI(openAPI);

        DefaultGenerator defaultGenerator = new DefaultGenerator();
        RstDocCodegen rstDocCodegen = new RstDocCodegen();
        rstDocCodegen.setTemplateDir("docs/source/schema/rst-documentation/");
        rstDocCodegen.setOutputDir("docs/source/schema/");
        ClientOptInput config = new ClientOptInput()
                .openAPI(openAPI)
                .config(rstDocCodegen);
        defaultGenerator.opts(config);
        defaultGenerator.generate();
    }

    @SuppressWarnings("rawtypes")
    private Map<String, Schema> getSchemaMap() {
        Map<String, Schema> stringSchemaMap = ModelConverters.getInstance().readAll(new AnnotatedType(LandscapeDescription.class));
        stringSchemaMap.keySet().forEach(s -> {
            Schema schema = stringSchemaMap.get(s);
            Map properties = schema.getProperties();
            if (properties != null) {
                //noinspection unchecked
                schema.setProperties(new TreeMap<String, Schema>(properties));
            }
        });
        return stringSchemaMap;
    }
}
