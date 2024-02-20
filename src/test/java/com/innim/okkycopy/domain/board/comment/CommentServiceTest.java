package com.innim.okkycopy.domain.board.comment;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentsResponse;
import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.repository.PostRepository;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.global.error.exception.NoSuchCommentException;
import com.innim.okkycopy.global.error.exception.NoSuchPostException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    CommentService commentService;


    @Nested
    class selectComments {
        @Test
        void given_notExistPostId_then_throwNoSuchPostException() {
            // given
            long notExistPostId = 1l;
            given(postRepository.findByPostId(notExistPostId)).willReturn(Optional.empty());

            // when
            Throwable thrown = catchThrowable(() -> {
                commentService.selectComments(notExistPostId);
            });

            // then
            then(postRepository).should(times(1)).findByPostId(notExistPostId);
            assertThat(thrown).isInstanceOf(NoSuchPostException.class);
        }

        @Test
        void given_correctInfo_then_returnCommentsResponse() {
            // given
            long existPostId = 1l;
            Comment existComment = Comment.builder()
                .likes(0l)
                .content("test content")
                .member(WithMockCustomUserSecurityContextFactory.customUserDetailsMock()
                    .getMember())
                .commentId(1l)
                .createdDate(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();

            Post existPost = Post.builder()
                .commentList(Arrays.asList(existComment))
                .build();

            given(postRepository.findByPostId(existPostId)).willReturn(Optional.of(existPost));

            // when
            CommentsResponse response = commentService.selectComments(existPostId);

            // then
            then(postRepository).should(times(1)).findByPostId(existPostId);
            assertThat(response.getComments().get(0).getContent()).isEqualTo(existComment.getContent());

        }
    }

    @Nested
    class selectReComments {
        @Test
        void given_notExistCommentId_then_throwNoSuchCommentException() {
            // given
            long notExistCommentId = 1l;
            given(commentRepository.findByCommentId(notExistCommentId)).willReturn(Optional.empty());

            // when
            Throwable thrown = catchThrowable(() -> {
                commentService.selectReComments(notExistCommentId);
            });

            // then
            then(commentRepository).should(times(1)).findByCommentId(notExistCommentId);
            assertThat(thrown).isInstanceOf(NoSuchCommentException.class);
        }

        @Test
        void given_correctInfo_then_returnCommentsResponse() {
            // given
            long existCommentId = 1L;

            Comment existComment = Comment.builder()
                    .likes(0l)
                    .content("test content")
                    .member(WithMockCustomUserSecurityContextFactory.customUserDetailsMock()
                            .getMember())
                    .commentId(1l)
                    .createdDate(LocalDateTime.now())
                    .lastUpdate(LocalDateTime.now())
                    .build();

            given(commentRepository.findByCommentId(existCommentId)).willReturn(Optional.of(existComment));
            given(commentRepository.findByParentId(existCommentId)).willReturn(Arrays.asList());

            // when
            CommentsResponse response = commentService.selectReComments(existCommentId);

            // then
            then(commentRepository).should(times(1)).findByCommentId(existCommentId);
            then(commentRepository).should(times(1)).findByParentId(existCommentId);
        }
    }
}
