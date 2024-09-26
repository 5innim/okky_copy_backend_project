package com.innim.okkycopy.integration.board.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.google.gson.Gson;
import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.common.annotation.WithMockCustomUser;
import com.innim.okkycopy.domain.board.comment.dto.request.CommentRequest;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.knowledge.KnowledgePostRepository;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("/board/comments/{id}")
public class _board_comments_$id {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    KnowledgePostRepository knowledgePostRepository;

    MockMvc mockMvc;

    @BeforeAll
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    @SqlGroup({
        @Sql("/data/init_member.sql"),
        @Sql("/data/init_post.sql"),
        @Sql("/data/init_comment.sql")
    })
    @Transactional
    @WithMockCustomUser
    void given_putRequest_then_response204() throws Exception {
        // given
        Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
        memberRepository.save(member);

        KnowledgePost knowledgePost = knowledgePost();
        knowledgePostRepository.save(knowledgePost);

        CommentRequest editedCommentRequest = commentRequest();
        editedCommentRequest.setContent("edited contents");

        mockMvc.perform(MockMvcRequestBuilders.post("/board/posts/1/comment")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(commentRequest())));

        // when
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put("/board/comments/1")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(editedCommentRequest))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

    }

    @Test
    @SqlGroup({
        @Sql("/data/init_member.sql"),
        @Sql("/data/init_post.sql"),
        @Sql("/data/init_comment.sql")
    })
    @Transactional
    @WithMockCustomUser
    void given_delRequest_then_response204() throws Exception {
        // given
        Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
        memberRepository.save(member);

        KnowledgePost knowledgePost = knowledgePost();
        knowledgePostRepository.save(knowledgePost);

        CommentRequest editedCommentRequest = commentRequest();
        editedCommentRequest.setContent("edited contents");

        mockMvc.perform(MockMvcRequestBuilders.post("/board/posts/1/comment")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(commentRequest())));

        // when
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.delete("/board/comments/1"))
            .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

    }

    CommentRequest commentRequest() {
        CommentRequest request = new CommentRequest();
        request.setContent("test_content");
        return request;
    }

    KnowledgePost knowledgePost() {
        return KnowledgePost.builder()
            .title("test_title")
            .lastUpdate(null)
            .tags(Collections.emptyList())
            .content("test_content")
            .boardTopic(BoardTopic.builder()
                .name("팁")
                .boardType(BoardType.builder()
                    .typeId(2L)
                    .name("지식")
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
