package de.bonndan.nivio.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest
public class LoginControllerTest {

    @Autowired
    private MockMvc mvc;
    private OAuth2AuthenticationToken principal;

    @BeforeEach
    public void setup() {
        principal = mock(OAuth2AuthenticationToken.class);
    }
//
//    @Test
//    void checkWhoAmI() throws Exception{
//
//        when(principal.getPrincipal().getAttribute("login")).thenReturn("test");
//        final MockHttpServletResponse response = mvc.perform(get("/user"))
//                .andReturn()
//                .getResponse();
//
//        final MockMvcRequestBuilders request = mvc.perform(get("/user"))
//                .principal(principal)
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.value());
//        assertThat(request.getContentAsString()).isEqualTo("githubUserId");
//    }

    @Test
    void checkShowLoginPage() throws Exception {
        final MockHttpServletResponse response = mvc.perform(get("/login")).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        final String contentTypeHeader = response.getHeader("Content-Type");
        assertThat(contentTypeHeader).isNotNull();
    }
}
