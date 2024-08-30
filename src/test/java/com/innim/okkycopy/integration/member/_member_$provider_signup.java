package com.innim.okkycopy.integration.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.google.gson.Gson;
import com.innim.okkycopy.domain.member.dto.request.OAuthMemberRequest;
import com.innim.okkycopy.global.auth.CustomOAuth2User;
import com.innim.okkycopy.global.util.EncryptionUtil;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("/member/{provider}/signup")
public class _member_$provider_signup {

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
    void given_invalidNickname_then_response400() throws Exception {
        // given
        OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();
        oAuthMemberRequest.setNickname("**");
        oAuthMemberRequest.setKey("test_key");


        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/member/google/signup")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(oAuthMemberRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @Transactional
    void given_request_then_response201() throws Exception {
        // given
        OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();
        oAuthMemberRequest.setNickname("testNickname");
        oAuthMemberRequest.setKey(EncryptionUtil.base64Encode("test_key"));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name_attribute_key", "provider_id");
        attributes.put("email", "test@test.com");
        attributes.put("name", "test_name");

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(null, attributes, "name_attribute_key", null, "google");
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("test_key", customOAuth2User);

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.post("/member/google/signup")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(oAuthMemberRequest))
                .session(mockHttpSession)
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

    }

    OAuthMemberRequest oAuthMemberRequest() {
        return OAuthMemberRequest.builder()
            .build();
    }
}
