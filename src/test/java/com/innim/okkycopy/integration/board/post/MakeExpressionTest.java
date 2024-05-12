package com.innim.okkycopy.integration.board.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class MakeExpressionTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    OkkyMemberRepository memberRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        initSecurityContext();
    }

    void initSecurityContext() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Member testMember = memberRepository.findById("test_id").get();
        CustomUserDetails principal = new CustomUserDetails(testMember);

        Authentication auth =
            UsernamePasswordAuthenticationToken.authenticated(principal, principal.getPassword(),
                principal.getAuthorities());
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Nested
    @Transactional
    class MakeLikeExpressionTest {

        @Test
        void given_noExistPost_then_responseErrorCode() throws Exception {
            // given
            long postId = 1000L;

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/board/posts/" + postId + "/like")
            );

            // then
            resultActions.andExpect(jsonPath("code").value(400021));
        }

        @Test
        void given_alreadyExistExpression_then_responseErrorCode() throws Exception {
            // given
            long postId = 1L;
            mockMvc.perform(
                MockMvcRequestBuilders.post("/board/posts/" + postId + "/hate")
            );

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/board/posts/" + postId + "/like")
            );

            // then
            resultActions.andExpect(jsonPath("code").value(400024));
        }

        @Test
        void given_correctInfo_then_response201() throws Exception {
            // given
            long postId = 1L;

            // when
            MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.post("/board/posts/" + postId + "/like")
            ).andReturn().getResponse();

            // then
            assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        }
    }

    @Nested
    @Transactional
    class MakeHateExpressionTest {

        @Test
        void given_noExistPost_then_responseErrorCode() throws Exception {
            // given
            long postId = 1000L;

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/board/posts/" + postId + "/hate")
            );

            // then
            resultActions.andExpect(jsonPath("code").value(400021));
        }

        @Test
        void given_alreadyExistExpression_then_responseErrorCode() throws Exception {
            // given
            long postId = 1L;
            mockMvc.perform(
                MockMvcRequestBuilders.post("/board/posts/" + postId + "/like")
            );

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/board/posts/" + postId + "/hate")
            );

            // then
            resultActions.andExpect(jsonPath("code").value(400024));
        }

        @Test
        void given_correctInfo_then_response201() throws Exception {
            // given
            long postId = 1L;

            // when
            MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.post("/board/posts/" + postId + "/hate")
            ).andReturn().getResponse();

            // then
            assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        }
    }


}
