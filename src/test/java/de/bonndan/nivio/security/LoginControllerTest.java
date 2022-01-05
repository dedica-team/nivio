package de.bonndan.nivio.security;

import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest
class LoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void checkShowLoginPage() throws Exception {
        final MockHttpServletResponse response = mvc.perform(get("/login")).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        final String contentTypeHeader = response.getHeader("Content-Type");
        assertThat(contentTypeHeader).isNotNull();
    }

}
