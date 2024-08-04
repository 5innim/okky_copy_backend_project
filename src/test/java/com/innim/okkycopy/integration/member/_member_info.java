package com.innim.okkycopy.integration.member;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.common.annotation.WithMockCustomUser;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("/member/info")
public class _member_info {

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
    @Sql("/data/init_member.sql")
    @Transactional
    @WithMockCustomUser
    void given_request_then_responseMemberDetailsResponse() throws Exception {
        // given
        Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
        memberRepository.save(member);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/member/info"));

        // then
        resultActions.andExpectAll(
            jsonPath("memberId").hasJsonPath(),
            jsonPath("nickname").hasJsonPath(),
            jsonPath("profile").hasJsonPath(),
            jsonPath("name").hasJsonPath(),
            jsonPath("accountFrom").hasJsonPath(),
            jsonPath("role").hasJsonPath(),
            jsonPath("email").hasJsonPath()
        );
    }



}
