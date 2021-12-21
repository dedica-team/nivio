package de.bonndan.nivio.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private HttpSecurity http;

    @Spy
    private final SecurityConfig securityConfig = new SecurityConfig();

    @BeforeEach
    public void setup() {
        http = mock(HttpSecurity.class);

    }

//    @Test
//    void configure() throws Exception {
//
//        securityConfig.configure(http);
//        securityConfig.configureForOptional(http);
//        securityConfig.configureForRequired(http);
//        securityConfig.configureForNone(http);
//
//        verify(securityConfig, times(1)).configure(http);
//        verify(securityConfig, times(1)).configureForOptional(http);
//        verify(securityConfig, times(1)).configureForRequired(http);
//        verify(securityConfig, times(1)).configureForNone(http);
//    }

}