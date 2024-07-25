package com.innim.okkycopy.integration.board.qna;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.google.gson.Gson;
import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.common.annotation.WithMockCustomUser;
import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.request.write.TagInfo;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.qna.entity.QnaPost;
import com.innim.okkycopy.domain.board.repository.PostRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("/board/qna/posts/{id}")
public class _board_qna_posts_$id {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;


    MockMvc mockMvc;

    @BeforeAll
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    @Transactional
    @WithMockCustomUser
    void given_invalidTopicWithPutRequest_then_response400() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        postRequest.setTopic("");

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/qna/posts/1")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @Transactional
    @WithMockCustomUser
    void given_invalidTitleWithPutRequest_then_response400() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        postRequest.setTitle("0123456789012345678901234567890");

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/qna/posts/1")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @Transactional
    @WithMockCustomUser
    void given_tagsBiggerThan3WithPutRequest_then_response400() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        postRequest.setTags(Arrays.asList(
            new TagInfo("A"),
            new TagInfo("B"),
            new TagInfo("C"),
            new TagInfo("D")));

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/qna/posts/1")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @Transactional
    @WithMockCustomUser
    void given_tagLengthBiggerThan10WithPutRequest_then_response400() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        postRequest.setTags(Arrays.asList(
            new TagInfo("A"),
            new TagInfo("B"),
            new TagInfo("01234567890")));

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/qna/posts/1")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @Transactional
    @WithMockCustomUser
    void given_invalidContentWithPutRequest_then_response400() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        postRequest.setContent("");

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/qna/posts/1")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @SqlGroup({
        @Sql("/data/init_member.sql"),
        @Sql("/data/init_post.sql")
    })
    @Transactional
    @WithMockCustomUser
    void given_putRequest_then_response204() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
        memberRepository.save(member);

        QnaPost qnaPost = qnaPost();
        postRepository.save(qnaPost);

        qnaPost.setMember(member);

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.put("/board/qna/posts/1")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @SqlGroup({
        @Sql("/data/init_member.sql"),
        @Sql("/data/init_post.sql")
    })
    @Transactional
    @WithMockCustomUser
    void given_getRequest_then_response200() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
        memberRepository.save(member);

        QnaPost qnaPost = qnaPost();
        postRepository.save(qnaPost);

        qnaPost.setMember(member);

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.get("/board/qna/posts/1")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @SqlGroup({
        @Sql("/data/init_member.sql"),
        @Sql("/data/init_post.sql")
    })
    @Transactional
    @WithMockCustomUser
    void given_delRequest_then_response204() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
        memberRepository.save(member);

        QnaPost qnaPost = qnaPost();
        postRepository.save(qnaPost);

        qnaPost.setMember(member);

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.delete("/board/qna/posts/1")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }


    PostRequest postRequest() {
        return PostRequest.builder()
            .title("test_title")
            .topic("기술")
            .tags(Collections.emptyList())
            .content("test_content")
            .build();
    }

    QnaPost qnaPost() {
        return QnaPost.builder()
            .title("test_title")
            .lastUpdate(null)
            .tags(Collections.emptyList())
            .content("test_content")
            .boardTopic(BoardTopic.builder()
                .name("기술")
                .boardType(BoardType.builder()
                    .typeId(2L)
                    .name("Q&A")
                    .build())
                .topicId(5L)
                .build())
            .comments(0)
            .views(0)
            .hates(0)
            .likes(0)
            .scraps(0)
            .build();
    }
}


