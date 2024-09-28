package com.innim.okkycopy.integration.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.google.gson.Gson;
import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("/member/signup")
public class _member_signup {

    @Autowired
    WebApplicationContext context;
    MockMvc mockMvc;

    @BeforeAll
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    void given_invalidId_then_response400() throws Exception {
        // given
        MemberRequest request = memberRequest();
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
        MemberRequest request = memberRequest();
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
        MemberRequest request = memberRequest();
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
        MemberRequest request = memberRequest();
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
        MemberRequest request = memberRequest();
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

    @Test
    @Transactional
    void given_request_then_response201() throws Exception {
        // given
        MemberRequest request = memberRequest();

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.post("/member/signup")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request))
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    MemberRequest memberRequest() {
        MemberRequest request = new MemberRequest();
        request.setId("test1234");
        request.setPassword("test1234**");
        request.setEmail("test@test.com");
        request.setEmailCheck(true);
        request.setName("testName");
        request.setNickname("testNickname");
        return request;
    }
}
