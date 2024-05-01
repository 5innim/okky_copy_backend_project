package com.innim.okkycopy.integration.board.comment;

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
public class RemoveExpressionTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private OkkyMemberRepository memberRepository;

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
    class RemoveLikeExpressionTest {

        @Test
        void given_noExistComment_then_responseErrorCode() throws Exception {
            // given
            long commentId = 1000L;

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/board/comments/" + commentId + "/like")
            );

            // then
            resultActions.andExpect(jsonPath("code").value(400023));
        }

        @Test
        void given_NoRegisterBefore_then_responseErrorCode() throws Exception {
            // given
            long commentId = 1L;

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/board/comments/" + commentId + "/like")
            );

            // then
            resultActions.andExpect(jsonPath("code").value(400025));
        }

        @Test
        void given_correctInfo_then_response204() throws Exception {
            // given
            long commentId = 1L;
            mockMvc.perform(
                MockMvcRequestBuilders.post("/board/comments/" + commentId + "/like")
            );

            // when
            MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.delete("/board/comments/" + commentId + "/like")
            ).andReturn().getResponse();

            // then
            assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }
    }

    @Nested
    @Transactional
    class RemoveHateExpressionTest {

        @Test
        void given_noExistComment_then_responseErrorCode() throws Exception {
            // given
            long commentId = 1000L;

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/board/comments/" + commentId + "/hate")
            );

            // then
            resultActions.andExpect(jsonPath("code").value(400023));
        }

        @Test
        void given_NoRegisterBefore_then_responseErrorCode() throws Exception {
            // given
            long commentId = 1L;

            // when
            ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/board/comments/" + commentId + "/hate")
            );

            // then
            resultActions.andExpect(jsonPath("code").value(400025));
        }

        @Test
        void given_correctInfo_then_response204() throws Exception {
            // given
            long commentId = 1L;
            mockMvc.perform(
                MockMvcRequestBuilders.post("/board/comments/" + commentId + "/hate")
            );

            // when
            MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.delete("/board/comments/" + commentId + "/hate")
            ).andReturn().getResponse();

            // then
            assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }
    }
}
