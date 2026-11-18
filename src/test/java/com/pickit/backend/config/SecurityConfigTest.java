package com.pickit.backend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    private static final String ALLOWED_ORIGIN = "http://localhost:5173";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void preflightFromAllowedOriginPassesBeforeLogin() throws Exception {
        mockMvc.perform(options("/api/me/interests")
                        .header("Origin", ALLOWED_ORIGIN)
                        .header("Access-Control-Request-Method", "PUT")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ALLOWED_ORIGIN));
    }

    @Test
    void preflightFromUnknownOriginIsRejected() throws Exception {
        mockMvc.perform(options("/api/me/interests")
                        .header("Origin", "https://unknown.example")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "authorization"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void requestWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/me/interests"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithInvalidTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/me/interests").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}
