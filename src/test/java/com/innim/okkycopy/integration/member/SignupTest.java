package com.innim.okkycopy.integration.member;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.google.gson.Gson;
import com.innim.okkycopy.domain.member.dto.request.MemberAddRequest;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class SignupTest {

    @Autowired
    WebApplicationContext context;
    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Nested
    @Transactional
    class ValidateInputTest {

        @Test
        void given_invalidId_then_response400() throws Exception {
            // given
            MemberAddRequest request = signupRequest();
            request.setId("id");

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new Gson().toJson(request))
            );

            // then
            resultActions.andExpect(jsonPath("status").value(400));
        }

        @Test
        void given_invalidPwd_then_response400() throws Exception {
            // given
            MemberAddRequest request = signupRequest();
            request.setPassword("pwd");

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new Gson().toJson(request))
            );

            // then
            resultActions.andExpect(jsonPath("status").value(400));
        }

        @Test
        void given_invalidEmail_then_response400() throws Exception {
            // given
            MemberAddRequest request = signupRequest();
            request.setEmail("test_email");

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new Gson().toJson(request))
            );

            // then
            resultActions.andExpect(jsonPath("status").value(400));
        }

        @Test
        void given_invalidName_then_response400() throws Exception {
            // given
            MemberAddRequest request = signupRequest();
            request.setName("t");

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new Gson().toJson(request))
            );

            // then
            resultActions.andExpect(jsonPath("status").value(400));
        }

        @Test
        void given_invalidNickname_then_response400() throws Exception {
            // given
            MemberAddRequest request = signupRequest();
            request.setNickname("t");

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new Gson().toJson(request))
            );

            // then
            resultActions.andExpect(jsonPath("status").value(400));
        }
    }

    /**
     * insertMember(SignupRequest signupRequest) in MemberService is Propagation.REQUIRES_NEW
     */

    private MemberAddRequest signupRequest() {
        return MemberAddRequest.builder()
            .id("test1234")
            .password("test1234**")
            .email("test@test.com")
            .name("testName")
            .nickname("testNickname")
            .emailCheck(true)
            .build();
    }
}
