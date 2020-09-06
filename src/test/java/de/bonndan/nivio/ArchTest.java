package de.bonndan.nivio;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

class ArchTest {
    @Test
    void do_not_access_STD_streams() {
        JavaClasses classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importClasspath();
        NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(classes);
    }

}


