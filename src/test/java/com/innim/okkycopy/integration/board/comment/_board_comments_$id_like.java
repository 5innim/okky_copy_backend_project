package com.innim.okkycopy.integration.board.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.common.annotation.WithMockCustomUser;
import com.innim.okkycopy.domain.board.comment.dto.request.CommentRequest;
import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.repository.CommentRepository;
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
@DisplayName("/board/comments/{id}/like")
public class _board_comments_$id_like {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    KnowledgePostRepository knowledgePostRepository;
    @Autowired
    CommentRepository commentRepository;

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
    void given_postRequest_then_response201() throws Exception {
        // given
        Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
        memberRepository.save(member);

        KnowledgePost knowledgePost = knowledgePost();
        knowledgePostRepository.save(knowledgePost);

        Comment comment = Comment.of(knowledgePost, member, commentRequest());
        commentRepository.save(comment);

        // when
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/board/comments/1/like"))
            .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
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

        Comment comment = Comment.of(knowledgePost, member, commentRequest());
        commentRepository.save(comment);

        mockMvc.perform(MockMvcRequestBuilders.post("/board/comments/1/like"));


        // when
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.delete("/board/comments/1/like"))
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
