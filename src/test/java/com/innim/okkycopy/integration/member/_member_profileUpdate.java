package com.innim.okkycopy.integration.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.google.gson.Gson;
import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.common.annotation.WithMockCustomUser;
import com.innim.okkycopy.domain.member.dto.request.ProfileUpdateRequest;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@DisplayName("/member/profile-update")
@TestInstance(Lifecycle.PER_CLASS)
public class _member_profileUpdate {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MemberRepository memberRepository;

    MockMvc mockMvc;

    @BeforeAll
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    @WithMockCustomUser
    void given_invalidName_then_response400() throws Exception {
        // given
        ProfileUpdateRequest profileUpdateRequest = profileUpdateRequest();
        profileUpdateRequest.setName("name!@#");

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.put("/member/profile-update")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(profileUpdateRequest))
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockCustomUser
    void given_invalidNickname_then_response400() throws Exception {
        // given
        ProfileUpdateRequest profileUpdateRequest = profileUpdateRequest();
        profileUpdateRequest.setNickname("name!@#");

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.put("/member/profile-update")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(profileUpdateRequest))
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }



    @Test
    @Sql("/data/init_member.sql")
    @WithMockCustomUser
    @Transactional
    void given_request_then_response204() throws Exception {
        // given
        Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
        memberRepository.save(member);
        ProfileUpdateRequest profileUpdateRequest = profileUpdateRequest();

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.put("/member/profile-update")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(profileUpdateRequest))
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    ProfileUpdateRequest profileUpdateRequest() {
        return ProfileUpdateRequest.builder()
            .name("testName")
            .nickname("testNickname")
            .profile(null)
            .build();
    }
}
