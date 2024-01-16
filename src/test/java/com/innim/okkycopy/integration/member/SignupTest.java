package com.innim.okkycopy.integration.member;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.google.gson.Gson;
import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class SignupTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Nested
    class validateInputTest {
        @Test
        void given_invalidId_then_responseErrorCode() throws Exception {
            // given
            SignupRequest request = signupRequest();
            request.setId("id");

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new Gson().toJson(request))
            );

            // then
            resultActions.andExpect(jsonPath("code", 400001).exists());
        }

        @Test
        void given_invalidPwd_then_responseErrorCode() throws Exception {
            // given
            SignupRequest request = signupRequest();
            request.setPassword("pwd");

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new Gson().toJson(request))
            );

            // then
            resultActions.andExpect(jsonPath("code", 400002).exists());
        }

        @Test
        void given_invalidEmail_then_responseErrorCode() throws Exception {
            // given
            SignupRequest request = signupRequest();
            request.setEmail("test_email");

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new Gson().toJson(request))
            );

            // then
            resultActions.andExpect(jsonPath("code", 400003).exists());
        }

        @Test
        void given_invalidName_then_responseErrorCode() throws Exception {
            // given
            SignupRequest request = signupRequest();
            request.setEmail("t");

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new Gson().toJson(request))
            );

            // then
            resultActions.andExpect(jsonPath("code", 400004).exists());
        }

        @Test
        void given_invalidNickname_then_responseErrorCode() throws Exception {
            // given
            SignupRequest request = signupRequest();
            request.setEmail("t");

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new Gson().toJson(request))
            );

            // then
            resultActions.andExpect(jsonPath("code", 400005).exists());
        }
    }

    @Test
    void given_correctUserInfo_then_returnBriefInfo() throws Exception {
        // given
        SignupRequest request = signupRequest();

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/member/signup")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(request)));

        // then
        resultActions.andExpectAll(
            jsonPath("nickname", "testNickname").exists(),
            jsonPath("name", "testName").exists(),
            jsonPath("email", "test@test.com").exists());
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
