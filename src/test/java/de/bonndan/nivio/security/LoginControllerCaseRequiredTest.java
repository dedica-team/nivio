package de.bonndan.nivio.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"nivio.loginMode=required"})
@AutoConfigureMockMvc
class LoginControllerCaseRequiredTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testShowLoginPageForRequired() throws Exception {

        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"));

    }

}
