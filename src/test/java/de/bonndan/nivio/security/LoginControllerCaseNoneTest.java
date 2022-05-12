package de.bonndan.nivio.security;

import de.bonndan.nivio.api.ApiController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"auth.loginMode=none"})
@AutoConfigureMockMvc
class LoginControllerCaseNoneTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testShowLoginPageForNone() throws Exception {

        mvc.perform(get(SecurityConfig.LOGIN_PATH))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().doesNotExist("Content-Type"));

    }

    @Test
    void testAllowsPostingWithCSRF() throws Exception {

        mvc.perform(post(ApiController.PATH + "/landscape").content("identifier:foo"))
                .andExpect(status().is2xxSuccessful());
    }

}
