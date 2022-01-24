package de.bonndan.nivio.security;

import de.bonndan.nivio.config.NivioConfigProperties;
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
@SpringBootTest(classes = {ValidationAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthConfigPropertiesTest {

    @Autowired
    private LocalValidatorFactoryBean localValidatorFactoryBean;

    @Test
    void defaultLogin() {
        // given
        AuthConfigProperties props = new AuthConfigProperties();
        // when
        Set<ConstraintViolation<AuthConfigProperties>> validate = localValidatorFactoryBean.getValidator().validate(props);
        // then
        assertThat(validate).isEmpty();
        assertThat(props.getLoginMode()).isEqualTo(SecurityConfig.LOGIN_MODE_NONE);
    }

    @Test
    void loginValidated() {
        // given
        AuthConfigProperties props = new AuthConfigProperties();
        // when
        props.setLoginMode("foo");
        Set<ConstraintViolation<AuthConfigProperties>> validate = localValidatorFactoryBean.getValidator().validate(props);
        // then
        assertThat(validate).isNotEmpty();
        ConstraintViolation<NivioConfigProperties>[] constraintViolations = (ConstraintViolation<NivioConfigProperties>[]) validate.toArray(ConstraintViolation[]::new);
        assertThat(constraintViolations[0].getMessage()).isEqualTo("Login mode must be one of none|optional|required");
    }

    @Test
    void allowedOrigins() {
        // given
        AuthConfigProperties props = new AuthConfigProperties();
        // when
        props.setLoginMode("required");
        props.setAllowedOriginPatterns("http://*.foo.com;https://*.domain1.com:[8080,8081]");
        Set<ConstraintViolation<AuthConfigProperties>> validate = localValidatorFactoryBean.getValidator().validate(props);
        // then
        assertThat(validate).isEmpty();
        assertThat(props.getAllowedOriginPatterns()).hasSize(2)
                .contains("http://*.foo.com")
                .contains("https://*.domain1.com:[8080,8081]");
    }
}
