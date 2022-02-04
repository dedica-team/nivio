package de.bonndan.nivio.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"auth.loginMode=none"})
@AutoConfigureMockMvc
class LoginControllerCaseNoneTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testShowLoginPageForNone() throws Exception {

        mvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().doesNotExist("Content-Type"));

    }

}
