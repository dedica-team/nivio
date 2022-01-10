package de.bonndan.nivio.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"nivio.loginMode=required"})
@AutoConfigureMockMvc
class LoginControllerTestForRequired {

    @Autowired
    private MockMvc mvc;

    @Value("${nivio.loginMode}")
    private String loginMode;

    @Test
    void testShowLoginPageForRequired() throws Exception {

        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"));

    }

}
