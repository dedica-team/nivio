package de.bonndan.nivio;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;

import static com.tngtech.archunit.lang.conditions.ArchConditions.callConstructor;
import static com.tngtech.archunit.lang.conditions.ArchConditions.callMethod;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

class ArchTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void load() {
        importedClasses = new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests()).importPackages("de.bonndan.nivio");
    }
    @Test
    void doNotAccessStandardStreams() {
        GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
                .because("output written directly to standard streams *might* be discarded by the starter of the Java process. You should use logging instead, which allows sophisticated configuration of the output channel.")
                .check(importedClasses);
    }

    @Test
    void doNotUseSystemExit() {
        noClasses().should(callMethod(System.class, "exit", Integer.TYPE))
                .because("the JVM should not be killed from anywhere in the code. You should implement procedures for a controlled shutdown.")
                .check(importedClasses);
    }

    @Test
    void provideCharsetWhenConvertingStringToBytes() {
        noClasses().should(callMethod(String.class, "getBytes"))
                .because("omitting the charset when converting a String to bytes falls back to the default charset, which is environment specific.")
                .check(importedClasses);
    }

    @Test
    void provideCharsetWhenConvertingStringFromBytes() {
        noClasses().should(callConstructor(String.class, byte[].class))
                .because("omitting the charset on String creation falls back to the default charset, which is environment specific.")
                .check(importedClasses);
    }

    @Test
    void noClassesShouldUseStandardLogging() {
        noClasses().should(GeneralCodingRules.USE_JAVA_UTIL_LOGGING);
    }

    @Test
     void checkBeansInApplicationClassTest() {
        methods().that().areDeclaredIn(Application.class)
                .should().notBeAnnotatedWith(Bean.class)
                .check(importedClasses);
    }

}


