package com.innim.okkycopy.unit.board.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.comment.dto.request.CommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.request.ReCommentRequest;
import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.repository.CommentExpressionRepository;
import com.innim.okkycopy.domain.board.comment.repository.CommentRepository;
import com.innim.okkycopy.domain.board.comment.service.CommentCrudService;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.repository.PostRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.common.storage.image_usage.ImageUsageService;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCode403Exception;
import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    @Mock
    CommentExpressionRepository commentExpressionRepository;
    @Mock
    EntityManager entityManager;
    @Mock
    ImageUsageService imageUsageService;
    @InjectMocks
    CommentCrudService commentCrudService;

    @Nested
    class _addComment_$CustomUserDetails_$CommentRequest_$long {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            CommentRequest commentRequest = commentRequest();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentCrudService.addComment(customUserDetails, commentRequest, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);

        }

        @Test
        void given_noExistPost_then_throwErrorCase400021() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            CommentRequest commentRequest = commentRequest();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(postRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentCrudService.addComment(customUserDetails, commentRequest, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();

            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_POST);

        }

        @Test
        void given_invoke_then_invokeSave() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            CommentRequest commentRequest = commentRequest();
            long postId = 1L;
            Post post = post();
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(postRepository.findByPostId(postId)).willReturn(Optional.of(post));

            // when
            commentCrudService.addComment(customUserDetails, commentRequest, postId);

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(commentRequest.getContent(), true);
            then(imageUsageService).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).save(any(Comment.class));
            then(commentRepository).shouldHaveNoMoreInteractions();

        }

        CommentRequest commentRequest() {
            return new CommentRequest("test_content");
        }

        Post post() {
            return Post.builder()
                .comments(1)
                .build();
        }
    }

    @Nested
    class _modifyComment_$CustomUserDetails_$CommentRequest_$long {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            CommentRequest commentRequest = commentRequest();
            long commentId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentCrudService.modifyComment(customUserDetails, commentRequest, commentId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);

        }

        @Test
        void given_noExistComment_then_throwErrorCase4000023() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            CommentRequest commentRequest = commentRequest();
            long commentId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentCrudService.modifyComment(customUserDetails, commentRequest, commentId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_COMMENT);

        }

        @Test
        void given_notEqualMemberWithCommentWriter_then_throwErrorCase403002() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            CommentRequest commentRequest = commentRequest();
            long commentId = 1L;
            Comment comment = comment();
            comment.setMember(Member.builder()
                .memberId(2L)
                .build());
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));

            // when
            Exception exception = catchException(() -> {
                commentCrudService.modifyComment(customUserDetails, commentRequest, commentId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode403Exception.class);
            assertThat(((StatusCode403Exception) exception).getErrorCase()).isEqualTo(ErrorCase._403_NO_AUTHORITY);

        }

        @Test
        void given_invoke_then_invokeUpdate() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            CommentRequest commentRequest = commentRequest();
            long commentId = 1L;
            Comment comment = comment();
            comment.setMember(customUserDetails.getMember());
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));

            // when
            commentCrudService.modifyComment(customUserDetails, commentRequest, commentId);

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1))
                .modifyImageUsages(comment().getContent(), commentRequest.getContent());
            then(imageUsageService).shouldHaveNoMoreInteractions();
            assertThat(comment.getContent()).isEqualTo(commentRequest.getContent());
        }

        CommentRequest commentRequest() {
            return new CommentRequest("test_content");
        }

        Comment comment() {
            return Comment.builder().build();
        }
    }

    @Nested
    class _removeComment_$CustomUserDetails_$long {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long commentId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentCrudService.removeComment(customUserDetails, commentId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);

        }

        @Test
        void given_noExistComment_then_throwErrorCase4000023() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long commentId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentCrudService.removeComment(customUserDetails, commentId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_COMMENT);

        }

        @Test
        void given_notEqualMemberWithCommentWriter_then_throwErrorCase403002() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long commentId = 1L;
            Comment comment = comment();
            comment.setMember(Member.builder()
                .memberId(2L)
                .build());
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));

            // when
            Exception exception = catchException(() -> {
                commentCrudService.removeComment(customUserDetails, commentId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode403Exception.class);
            assertThat(((StatusCode403Exception) exception).getErrorCase()).isEqualTo(ErrorCase._403_NO_AUTHORITY);

        }

        @Test
        void given_invoke_then_invokeFindByParentIdAndRemove() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long commentId = 1L;
            Comment comment = comment();
            comment.setMember(customUserDetails.getMember());
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));
            given(commentRepository.findByParentId(comment.getCommentId())).willReturn(Collections.emptyList());

            // when
            commentCrudService.removeComment(customUserDetails, commentId);

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).should(times(1)).findByParentId(comment.getCommentId());
            then(commentRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(comment.getContent(), false);
            then(imageUsageService).shouldHaveNoMoreInteractions();
            then(entityManager).should(times(1)).remove(any(Comment.class));
            then(entityManager).shouldHaveNoMoreInteractions();


        }

        Comment comment() {
            return Comment.builder()
                .commentId(1L)
                .post(
                    Post.builder()
                        .comments(1)
                        .build()
                )
                .build();
        }
    }

    @Nested
    class _findComments_$CustomUserDetails_$long {

        @Test
        void given_noExistPost_then_throwErrorCase400021() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long postId = 1L;
            given(postRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentCrudService.findComments(customUserDetails, postId);
            });

            // then
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_POST);
        }

        @Test
        void given_noAuthentication_then_notFindCommentExpression() {
            // given
            CustomUserDetails customUserDetails = null;
            long postId = 1L;
            Post post = post();
            given(postRepository.findByPostId(postId)).willReturn(Optional.of(post));

            // when
            Exception exception = catchException(() -> {
                commentCrudService.findComments(customUserDetails, postId);
            });

            // then
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            then(commentExpressionRepository).shouldHaveNoInteractions();
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NO_SUCH_COMMENT); // exception generated from findReComments
        }

        @Test
        void given_authentication_then_findCommentExpression() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long postId = 1L;
            Post post = post();
            given(postRepository.findByPostId(postId)).willReturn(Optional.of(post));

            // when
            Exception exception = catchException(() -> {
                commentCrudService.findComments(customUserDetails, postId);
            });

            // then
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            then(commentExpressionRepository).should(times(1))
                .findByMemberAndComment(post.getCommentList().get(0), customUserDetails.getMember());
            then(commentExpressionRepository).shouldHaveNoMoreInteractions();
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NO_SUCH_COMMENT); // exception generated from findReComments

        }

        Post post() {
            Comment comment = Comment.builder()
                .commentId(1L)
                .depth(1)
                .build();

            return Post.builder()
                .commentList(Arrays.asList(comment))
                .build();
        }
    }

    @Nested
    class _addReComment_$CustomUserDetails_$long_$ReCommentRequest {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long commentId = 1L;
            ReCommentRequest reCommentRequest = reCommentRequest();
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentCrudService.addReComment(customUserDetails, commentId, reCommentRequest);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);

        }

        @Test
        void given_noExistComment_then_throwErrorCase400023() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long postId = 1L;
            long commentId = 1L;
            ReCommentRequest reCommentRequest = reCommentRequest();
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentCrudService.addReComment(customUserDetails, commentId, reCommentRequest);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_COMMENT);

        }

        @Test
        void given_commentDepthIsBiggerThan1_then_throwErrorCase400026() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long commentId = 1L;
            ReCommentRequest reCommentRequest = reCommentRequest();
            Comment comment = comment();
            comment.setDepth(2);
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));

            // when
            Exception exception = catchException(() -> {
                commentCrudService.addReComment(customUserDetails, commentId, reCommentRequest);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NOT_SUPPORTED_CASE);

        }

        @Test
        void given_invoke_then_invokeSave() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long commentId = 1L;
            ReCommentRequest reCommentRequest = reCommentRequest();
            Comment comment = comment();
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));

            // when
            commentCrudService.addReComment(customUserDetails, commentId, reCommentRequest);

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).should(times(1)).save(any(Comment.class));
            then(commentRepository).shouldHaveNoMoreInteractions();

        }

        Post post() {
            return Post.builder()
                .postId(1L)
                .build();
        }

        Comment comment() {
            return Comment.builder()
                .commentId(1L)
                .depth(1)
                .build();
        }

        ReCommentRequest reCommentRequest() {
            return ReCommentRequest.builder()
                .content("test_content")
                .mentionId(1L)
                .build();
        }
    }

    @Nested
    class _findReComments_$CustomUserDetails_$long {

        @Test
        void given_noExistComment_then_throwErrorCase4000023() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long commentId = 1L;
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentCrudService.findReComments(customUserDetails, commentId);
            });

            // then
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_COMMENT);
        }

        @Test
        void given_noAuthentication_then_notFindCommentExpression() {
            // given
            CustomUserDetails customUserDetails = null;
            long commentId = 1L;
            List<Comment> comments = comments();
            given(commentRepository.findByCommentId(commentId)).willReturn(
                Optional.of(comment()));
            given(commentRepository.findByParentId(commentId)).willReturn(comments);

            // when
            commentCrudService.findReComments(customUserDetails, commentId);

            // then
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).should(times(1)).findByParentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            then(commentExpressionRepository).shouldHaveNoInteractions();

        }

        @Test
        void given_authentication_then_findCommentExpression() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long commentId = 1L;
            List<Comment> comments = comments();
            given(commentRepository.findByCommentId(commentId)).willReturn(
                Optional.of(comment()));
            given(commentRepository.findByParentId(commentId)).willReturn(comments);

            // when
            commentCrudService.findReComments(customUserDetails, commentId);

            // then
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).should(times(1)).findByParentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            then(commentExpressionRepository).should(times(1))
                .findByMemberAndComment(any(Comment.class), any(Member.class));
            then(commentExpressionRepository).shouldHaveNoMoreInteractions();

        }

        Comment comment() {
            return Comment.builder().commentId(1L).build();
        }

        List<Comment> comments() {
            Comment comment = Comment.builder()
                .commentId(2L)
                .build();
            return Collections.singletonList(comment);
        }
    }


}
