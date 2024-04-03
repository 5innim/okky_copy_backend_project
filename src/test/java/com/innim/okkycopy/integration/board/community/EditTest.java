package com.innim.okkycopy.integration.board.community;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.google.gson.Gson;
import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import java.util.Arrays;
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
public class EditTest {

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
    void given_noExistPost_then_responseErrorCode() throws Exception {
        // given
        PostRequest updateRequest = postRequest();
        long postId = 1000L;

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/community/posts/" + postId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(updateRequest))
        );

        // then
        resultActions.andExpect(jsonPath("code").value(400021));
    }

    @Test
    @Transactional
    void given_noEqualPostWriterWithAuthenticationPrincipal_then_responseErrorCode() throws Exception {
        // given
        PostRequest updateRequest = postRequest();
        updateRequest.setTopic("사는얘기");
        long postId = 4L;

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/community/posts/" + postId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(updateRequest))
        );

        // then
        resultActions.andExpect(jsonPath("code").value(403002));
    }

    @Test
    @Transactional
    void given_noExistTopic_then_responseErrorCode() throws Exception {
        // given
        PostRequest updateRequest = postRequest();
        long postId = 3L;

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/community/posts/" + postId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(updateRequest))
        );

        // then
        resultActions.andExpect(jsonPath("code").value(400020));

    }

    @Test
    @Transactional
    void given_correctUpdateInfo_then_response204() throws Exception {
        // given
        PostRequest updateRequest = postRequest();
        updateRequest.setTopic("사는얘기");
        long postId = 3L;

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/community/posts/" + postId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(updateRequest))
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Transactional
    void given_notMatchedTopic_then_response400031() throws Exception {
        // given
        PostRequest notMatchedPostRequest = notMatchedPostRequest();
        long postId = 3L;
        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/community/posts/" + postId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(new Gson().toJson(notMatchedPostRequest))
        );

        // then
        resultActions.andExpect(jsonPath("code").value("400031"));
    }

    PostRequest postRequest() {
        return PostRequest.builder()
            .title("test_title")
            .topic("test_topic")
            .tags(Arrays.asList())
            .content("test_content")
            .build();
    }

    PostRequest notMatchedPostRequest() {
        return PostRequest.builder()
            .title("test_title")
            .topic("기술")
            .tags(Arrays.asList())
            .content("test_content")
            .build();
    }

}