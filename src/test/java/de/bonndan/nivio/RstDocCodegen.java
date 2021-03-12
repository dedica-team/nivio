package de.bonndan.nivio;

import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenType;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.meta.GeneratorMetadata;
import org.openapitools.codegen.meta.Stability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.openapitools.codegen.utils.StringUtils.escape;

public class RstDocCodegen extends DefaultCodegen implements CodegenConfig {

    public static final String PROJECT_NAME = "projectName";

    private static final Logger LOGGER = LoggerFactory.getLogger(RstDocCodegen.class);

    public CodegenType getTag() {
        return CodegenType.DOCUMENTATION;
    }

    public String getName() {
        return "restructuredText";
    }

    public String getHelp() {
        return "Generates a rst documentation.";
    }

    public RstDocCodegen() {
        super();

        generatorMetadata = GeneratorMetadata.newBuilder(generatorMetadata)
                .stability(Stability.BETA)
                .build();

        outputFolder = "generated-code" + File.separator + "rst";
        modelTemplateFiles.put("model.mustache", ".rst");
        apiTemplateFiles.put("api.mustache", ".rst");
        embeddedTemplateDir = templateDir = "rst-documentation";
        //apiPackage = File.separator + "Apis";
        modelPackage = "Models";
        supportingFiles.add(new SupportingFile("README.mustache", "", "README.rst"));
        // TODO: Fill this out.
    }

    @Override
    protected void initializeSpecialCharacterMapping() {
        // escape only those symbols that can mess up markdown
        specialCharReplacements.put("\\", "\\\\");
        specialCharReplacements.put("/", "\\/");
        specialCharReplacements.put("`", "\\`");
        specialCharReplacements.put("*", "\\*");
        specialCharReplacements.put("_", "\\_");
        specialCharReplacements.put("[", "\\[");
        specialCharReplacements.put("]", "\\]");

        // todo Current markdown api and model mustache templates display properties and parameters in tables. Pipe
        //  symbol in a table can be commonly escaped with a backslash (e.g. GFM supports this). However, in some cases
        //  it may be necessary to choose a different approach.
        specialCharReplacements.put("|", "\\|");
    }

    /**
     * Works identically to {@link DefaultCodegen#toParamName(String)} but doesn't camelize.
     *
     * @param name Codegen property object
     * @return the sanitized parameter name
     */
    @Override
    public String toParamName(String name) {
        if (reservedWords.contains(name)) {
            return escapeReservedWord(name);
        } else if (((CharSequence) name).chars().anyMatch(character -> specialCharReplacements.keySet().contains("" + ((char) character)))) {
            return escape(name, specialCharReplacements, null, null);
        }
        return name;
    }
}
