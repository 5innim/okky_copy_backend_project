package com.innim.okkycopy.domain.board.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentListResponse;
import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.repository.CommentRepository;
import com.innim.okkycopy.domain.board.comment.service.CommentCrudService;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.repository.PostRepository;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentCrudServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    CommentCrudService commentCrudService;


    @Nested
    class SelectComments {

        @Test
        void given_notExistPostId_then_throwStatusCode400Exception() {
            // given
            long notExistPostId = 1L;
            given(postRepository.findByPostId(notExistPostId)).willReturn(Optional.empty());

            // when
            Throwable thrown = catchThrowable(() -> {
                commentCrudService.findComments(null, notExistPostId);
            });

            // then
            then(postRepository).should(times(1)).findByPostId(notExistPostId);
            assertThat(thrown).isInstanceOf(StatusCode400Exception.class);
        }

        @Test
        void given_correctInfo_then_returnCommentsResponse() {
            // given
            long existPostId = 1L;
            Comment existComment = Comment.builder()
                .likes(0)
                .content("test content")
                .member(WithMockCustomUserSecurityContextFactory.customUserDetailsMock()
                    .getMember())
                .commentId(1L)
                .createdDate(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();

            Post existPost = Post.builder()
                .commentList(Arrays.asList(existComment))
                .build();

            given(postRepository.findByPostId(existPostId)).willReturn(Optional.of(existPost));

            // when
            CommentListResponse response = commentCrudService.findComments(null, existPostId);

            // then
            then(postRepository).should(times(1)).findByPostId(existPostId);
            assertThat(response.getComments().get(0).getContent()).isEqualTo(existComment.getContent());

        }
    }

    @Nested
    class SelectReComments {

        @Test
        void given_notExistCommentId_then_throwStatusCode400Exception() {
            // given
            long notExistCommentId = 1L;
            given(commentRepository.findByCommentId(notExistCommentId)).willReturn(Optional.empty());

            // when
            Throwable thrown = catchThrowable(() -> {
                commentCrudService.findReComments(null, notExistCommentId);
            });

            // then
            then(commentRepository).should(times(1)).findByCommentId(notExistCommentId);
            assertThat(thrown).isInstanceOf(StatusCode400Exception.class);
        }

        @Test
        void given_correctInfo_then_returnCommentsResponse() {
            // given
            long existCommentId = 1L;

            Comment existComment = Comment.builder()
                .likes(0)
                .content("test content")
                .member(WithMockCustomUserSecurityContextFactory.customUserDetailsMock()
                    .getMember())
                .commentId(1L)
                .createdDate(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();

            given(commentRepository.findByCommentId(existCommentId)).willReturn(Optional.of(existComment));
            given(commentRepository.findByParentId(existCommentId)).willReturn(Arrays.asList());

            // when
            commentCrudService.findReComments(null, existCommentId);

            // then
            then(commentRepository).should(times(1)).findByCommentId(existCommentId);
            then(commentRepository).should(times(1)).findByParentId(existCommentId);
        }
    }
}
