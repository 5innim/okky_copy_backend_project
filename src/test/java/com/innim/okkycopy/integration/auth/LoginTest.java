package com.innim.okkycopy.integration.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.google.gson.Gson;
import com.innim.okkycopy.global.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class LoginTest {

    @Autowired
    WebApplicationContext context;
    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    void given_correctCertificate_then_responseJwt() throws Exception {
        // given
        // init_test_data procedure success

        // when
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(new LoginContent("test_id", "test1234**")))
        ).andReturn().getResponse();

        // then
        String accessToken = response.getCookie("accessToken").getValue();
        String refreshToken = response.getCookie("refreshToken").getValue();

        Throwable thrown = catchThrowable(() -> {
            JwtUtil.validateToken(accessToken);
            JwtUtil.validateToken(refreshToken);
        });
        assertThat(thrown).isNull();


    }

    private class LoginContent {

        String id;
        String password;

        public LoginContent(String id, String password) {
            this.id = id;
            this.password = password;
        }
    }
}
