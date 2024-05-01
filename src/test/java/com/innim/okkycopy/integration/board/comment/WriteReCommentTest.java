package com.innim.okkycopy.integration.board.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.google.gson.Gson;
import com.innim.okkycopy.domain.board.comment.dto.request.ReCommentRequest;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
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
public class WriteReCommentTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    OkkyMemberRepository memberRepository;
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
    void given_invalidContent_then_response400() throws Exception {
        // given
        long postId = 1L;
        long commentId = 1L;
        ReCommentRequest reCommentRequest = reCommentRequest();
        reCommentRequest.setContent("");

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/posts/" + postId + "/comments/" + commentId + "/recomment")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(reCommentRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @Transactional
    void given_invalidMentionId_then_response400() throws Exception {
        // given
        long postId = 1L;
        long commentId = 1L;
        ReCommentRequest reCommentRequest = reCommentRequest();
        reCommentRequest.setMentionId(0L);

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/posts/" + postId + "/comments/" + commentId + "/recomment")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(reCommentRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }


    @Test
    @Transactional
    void given_noExistPost_then_responseErrorCode() throws Exception {
        // given
        long postId = 1000L;
        long commentId = 1L;

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/posts/" + postId + "/comments/" + commentId + "/recomment")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(reCommentRequest()))
        );

        // then
        resultActions.andExpect(jsonPath("code").value(400021));
    }

    @Test
    @Transactional
    void given_noExistComment_then_responseErrorCode() throws Exception {
        // given
        long postId = 1L;
        long commentId = 1000L;

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/posts/" + postId + "/comments/" + commentId + "/recomment")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(reCommentRequest()))
        );

        // then
        resultActions.andExpect(jsonPath("code").value(400023));
    }

    @Test
    @Transactional
    void given_correctInfo_then_response201() throws Exception {
        // given
        long postId = 1L;
        long commentId = 1L;

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/posts/" + postId + "/comments/" + commentId + "/recomment")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(reCommentRequest()))
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    ReCommentRequest reCommentRequest() {
        return ReCommentRequest.builder()
            .content("test content")
            .mentionId(1L)
            .build();
    }


}
