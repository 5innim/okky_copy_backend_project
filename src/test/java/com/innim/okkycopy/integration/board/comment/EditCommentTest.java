package com.innim.okkycopy.integration.board.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.google.gson.Gson;
import com.innim.okkycopy.domain.board.comment.dto.request.CommentAddRequest;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
public class EditCommentTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MemberRepository memberRepository;

    MockMvc mockMvc;

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

    @Test
    @Transactional
    void given_noExistComment_then_responseErrorCode() throws Exception {
        // given
        long commentId = 1000L;
        CommentAddRequest commentAddRequest = commentRequest();

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(commentAddRequest))
        );

        // then
        resultActions.andExpect(jsonPath("code").value(400023));
    }

    @Test
    @Transactional
    void given_noEqualCommentWriterWithAuthenticationPrincipal_then_responseErrorCode() throws Exception {
        // given
        CommentAddRequest commentAddRequest = commentRequest();
        long commentId = 2L;

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(commentAddRequest))
        );

        // then
        resultActions.andExpect(jsonPath("code").value(403002));
    }

    @Test
    @Transactional
    void given_correctUpdateInfo_then_response204() throws Exception {
        // given
        CommentAddRequest commentAddRequest = commentRequest();
        long commentId = 1L;

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(commentAddRequest))
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    CommentAddRequest commentRequest() {
        return new CommentAddRequest("test comment");
    }
}
