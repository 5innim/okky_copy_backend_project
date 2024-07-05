package com.innim.okkycopy.unit.board.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.entity.CommentExpression;
import com.innim.okkycopy.domain.board.comment.repository.CommentExpressionRepository;
import com.innim.okkycopy.domain.board.comment.repository.CommentRepository;
import com.innim.okkycopy.domain.board.comment.service.CommentExpressionService;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.qna.entity.QnaPost;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentExpressionServiceTest {

    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentExpressionRepository commentExpressionRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    EntityManager entityManager;
    @InjectMocks
    CommentExpressionService commentExpressionService;

    @Nested
    class _addCommentExpression_$Member_$long_$ExpressionType {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long commentId = 1L;
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentExpressionService.addCommentExpression(member, commentId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);

        }

        @Test
        void given_noExistComment_then_throwErrorCase400023() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long commentId = 1L;
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentExpressionService.addCommentExpression(member, commentId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_COMMENT);

        }

        @Test
        void given_expressionAlreadyExist_then_throwErrorCase400028() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long commentId = 1L;
            Comment comment = comment();
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));
            given(commentExpressionRepository.findByMemberAndComment(comment, member)).willReturn(
                Optional.of(commentExpression()));

            // when
            Exception exception = catchException(() -> {
                commentExpressionService.addCommentExpression(member, commentId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            then(commentExpressionRepository).should(times(1)).findByMemberAndComment(comment, member);
            then(commentExpressionRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_ALREADY_EXIST_EXPRESSION);

        }

        @Test
        void given_commentInQnaPostAndDepthIsBiggerThan1_then_throwErrorCase400026() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long commentId = 1L;
            Comment comment = comment();
            Post post = qnaPost();
            comment.setPost(post);
            comment.setDepth(2);
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));
            given(commentExpressionRepository.findByMemberAndComment(comment, member)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentExpressionService.addCommentExpression(member, commentId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            then(commentExpressionRepository).should(times(1)).findByMemberAndComment(comment, member);
            then(commentExpressionRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NOT_SUPPORTED_CASE);

        }

        @Test
        void given_invoke_then_invokeSave() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long commentId = 1L;
            Comment comment = comment();
            Post post = knowledgePost();
            comment.setPost(post);
            comment.setLikes(0);
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));
            given(commentExpressionRepository.findByMemberAndComment(comment, member)).willReturn(Optional.empty());

            // when
            commentExpressionService.addCommentExpression(member, commentId, expressionType);

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            then(commentExpressionRepository).should(times(1)).findByMemberAndComment(comment, member);
            then(commentExpressionRepository).should(times(1)).save(any(CommentExpression.class));
            then(commentExpressionRepository).shouldHaveNoMoreInteractions();

        }

        Comment comment() {
            return Comment.builder()
                .commentId(1L)
                .build();
        }

        CommentExpression commentExpression() {
            return CommentExpression.builder()
                .expressionId(1L)
                .expressionType(ExpressionType.LIKE)
                .build();
        }

        Post qnaPost() {
            return QnaPost.builder()
                .build();
        }

        Post knowledgePost() {
            return KnowledgePost.builder()
                .build();
        }
    }

    @Nested
    class _removeCommentExpression_$Member_$long_$ExpressionType {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long commentId = 1L;
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentExpressionService.removeCommentExpression(member, commentId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);

        }

        @Test
        void given_noExistComment_then_throwErrorCase400023() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long commentId = 1L;
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentExpressionService.removeCommentExpression(member, commentId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_COMMENT);

        }

        @Test
        void given_noExistCommentExpression_then_throwErrorCase400025() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long commentId = 1L;
            Comment comment = comment();
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));
            given(commentExpressionRepository.findByMemberAndComment(comment, member)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                commentExpressionService.removeCommentExpression(member, commentId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            then(commentExpressionRepository).should(times(1)).findByMemberAndComment(comment, member);
            then(commentExpressionRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NOT_REGISTERED_BEFORE);

        }

        @Test
        void given_notEqualCommentExpression_then_throwErrorCase400025() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long commentId = 1L;
            Comment comment = comment();
            ExpressionType expressionType = ExpressionType.LIKE;
            CommentExpression commentExpression = commentExpression();
            commentExpression.setExpressionType(ExpressionType.HATE);
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));
            given(commentExpressionRepository.findByMemberAndComment(comment, member)).willReturn(
                Optional.of(commentExpression));

            // when
            Exception exception = catchException(() -> {
                commentExpressionService.removeCommentExpression(member, commentId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            then(commentExpressionRepository).should(times(1)).findByMemberAndComment(comment, member);
            then(commentExpressionRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NOT_REGISTERED_BEFORE);

        }

        @Test
        void given_invoke_then_invokeRemove() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long commentId = 1L;
            Comment comment = comment();
            comment.setLikes(1);
            ExpressionType expressionType = ExpressionType.LIKE;
            CommentExpression commentExpression = commentExpression();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(commentRepository.findByCommentId(commentId)).willReturn(Optional.of(comment));
            given(commentExpressionRepository.findByMemberAndComment(comment, member)).willReturn(
                Optional.of(commentExpression));

            // when

            commentExpressionService.removeCommentExpression(member, commentId, expressionType);

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(commentRepository).should(times(1)).findByCommentId(commentId);
            then(commentRepository).shouldHaveNoMoreInteractions();
            then(commentExpressionRepository).should(times(1)).findByMemberAndComment(comment, member);
            then(commentExpressionRepository).shouldHaveNoMoreInteractions();
            then(entityManager).should(times(1)).remove(commentExpression);
            then(entityManager).shouldHaveNoMoreInteractions();

        }

        Comment comment() {
            return Comment.builder()
                .commentId(1L)
                .build();
        }

        CommentExpression commentExpression() {
            return CommentExpression.builder()
                .expressionType(ExpressionType.LIKE)
                .build();
        }
    }
}
