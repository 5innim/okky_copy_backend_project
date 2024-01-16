package com.innim.okkycopy.integration.auth;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.assertj.core.api.Assertions.*;

import com.google.gson.Gson;
import com.innim.okkycopy.domain.member.MemberService;
import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.global.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class LoginTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MemberService service;
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
        SignupRequest signupRequest = signupRequest();
        service.insertMember(signupRequest);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(new LoginContent("test1234", "test1234**")))
        );

        // then
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
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

    private SignupRequest signupRequest() {
        return SignupRequest.builder()
            .id("test1234")
            .password("test1234**")
            .email("test@test.com")
            .name("testName")
            .nickname("testNickname")
            .emailCheck(true)
            .build();
    }





}
