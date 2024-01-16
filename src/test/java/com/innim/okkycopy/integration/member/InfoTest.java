package com.innim.okkycopy.integration.member;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.common.annotation.WithMockCustomUser;
import com.innim.okkycopy.domain.member.MemberRepository;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class InfoTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MemberRepository repository;
    MockMvc mockMvc;

    @BeforeEach
    @Transactional
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    @WithMockCustomUser
    void given_authorizedMember_then_responseMemberInfo() throws Exception {
        // given
        repository.save(WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember());

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/member/info"));

        // then
        resultActions.andExpectAll(
            jsonPath("memberId", 1l).exists(),
            jsonPath("nickname", "test_nickname").exists(),
            jsonPath("scrappedPost", new ArrayList()).exists()
            );
    }

}
