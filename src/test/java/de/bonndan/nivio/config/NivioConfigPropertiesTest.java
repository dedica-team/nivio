package de.bonndan.nivio.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { ValidationAutoConfiguration.class })
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NivioConfigPropertiesTest {

    @Autowired
    private LocalValidatorFactoryBean localValidatorFactoryBean;

    @Test
    void checksBackgroundColor() {

        NivioConfigProperties props = new NivioConfigProperties();
        props.setBrandingBackground("*!W13");
        Set<ConstraintViolation<NivioConfigProperties>> validate = localValidatorFactoryBean.getValidator().validate(props);

        assertThat(validate).isNotEmpty();
        ConstraintViolation<NivioConfigProperties>[] constraintViolations = (ConstraintViolation<NivioConfigProperties>[]) validate.toArray(ConstraintViolation[]::new);
        assertThat(constraintViolations[0].getMessage()).isEqualTo("brandingBackground must be a hex color code");
    }

    @Test
    void checksForegroundColor() {

        NivioConfigProperties props = new NivioConfigProperties();
        props.setBrandingForeground("*!W13");
        Set<ConstraintViolation<NivioConfigProperties>> validate = localValidatorFactoryBean.getValidator().validate(props);

        assertThat(validate).isNotEmpty();
        ConstraintViolation<NivioConfigProperties>[] constraintViolations = (ConstraintViolation<NivioConfigProperties>[]) validate.toArray(ConstraintViolation[]::new);
        assertThat(constraintViolations[0].getMessage()).isEqualTo("brandingForeground must be a hex color code");
    }

    @Test
    void checksSecondaryColor() {

        NivioConfigProperties props = new NivioConfigProperties();
        props.setBrandingSecondary("*!W13");
        Set<ConstraintViolation<NivioConfigProperties>> validate = localValidatorFactoryBean.getValidator().validate(props);

        assertThat(validate).isNotEmpty();
        ConstraintViolation<NivioConfigProperties>[] constraintViolations = (ConstraintViolation<NivioConfigProperties>[]) validate.toArray(ConstraintViolation[]::new);
        assertThat(constraintViolations[0].getMessage()).isEqualTo("brandingSecondary must be a hex color code");
    }

    @Test
    void checksBaseUrl() {

        NivioConfigProperties props = new NivioConfigProperties();
        props.setBaseUrl("*!W13");
        Set<ConstraintViolation<NivioConfigProperties>> validate = localValidatorFactoryBean.getValidator().validate(props);

        assertThat(validate).isNotEmpty();
        ConstraintViolation<NivioConfigProperties>[] constraintViolations = (ConstraintViolation<NivioConfigProperties>[]) validate.toArray(ConstraintViolation[]::new);
        assertThat(constraintViolations[0].getMessage()).isEqualTo("baseUrl must be a valid URL");
    }

    @Test
    void checksLogoUrl() {

        NivioConfigProperties props = new NivioConfigProperties();
        props.setBrandingLogoUrl("*!W13");
        Set<ConstraintViolation<NivioConfigProperties>> validate = localValidatorFactoryBean.getValidator().validate(props);

        assertThat(validate).isNotEmpty();
        ConstraintViolation<NivioConfigProperties>[] constraintViolations = (ConstraintViolation<NivioConfigProperties>[]) validate.toArray(ConstraintViolation[]::new);
        assertThat(constraintViolations[0].getMessage()).isEqualTo("brandingLogoUrl must be a valid URL");
    }

    @Test
    void checksBrandingMessage() {

        NivioConfigProperties props = new NivioConfigProperties();
        props.setBrandingMessage("*!W13");
        Set<ConstraintViolation<NivioConfigProperties>> validate = localValidatorFactoryBean.getValidator().validate(props);

        assertThat(validate).isNotEmpty();
        ConstraintViolation<NivioConfigProperties>[] constraintViolations = (ConstraintViolation<NivioConfigProperties>[]) validate.toArray(ConstraintViolation[]::new);
        assertThat(constraintViolations[0].getMessage()).isEqualTo("brandingMessage must be a valid string");
    }

    @Test
    void checkSeed()
    {
        NivioConfigProperties props = new NivioConfigProperties();
        props.setSeed("*!W13");
        Set<ConstraintViolation<NivioConfigProperties>> validate = localValidatorFactoryBean.getValidator().validate(props);

        assertThat(validate).isNotEmpty();
        ConstraintViolation<NivioConfigProperties>[] constraintViolations = (ConstraintViolation<NivioConfigProperties>[]) validate.toArray(ConstraintViolation[]::new);
        assertThat(constraintViolations[0].getMessage()).isEqualTo("seed must be a valid landscape configuration");
    }

}