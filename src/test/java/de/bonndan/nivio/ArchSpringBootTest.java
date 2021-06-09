package de.bonndan.nivio;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.context.annotation.Bean;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;


@AnalyzeClasses(packages = "de.bonndan.nivio")
class ArchSpringBootTest {


    @ArchTest
    public static final ArchRule checkBeansInApplicationClassTest = methods().that().areDeclaredIn(Application.class).should().notBeAnnotatedWith(Bean.class);
}