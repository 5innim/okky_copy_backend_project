package com.innim.okkycopy.unit.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.PostExpression;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.repository.PostExpressionRepository;
import com.innim.okkycopy.domain.board.repository.PostRepository;
import com.innim.okkycopy.domain.board.service.PostExpressionService;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostExpressionServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    PostExpressionRepository postExpressionRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    @PersistenceContext
    EntityManager entityManager;
    @InjectMocks
    PostExpressionService postExpressionService;

    @Nested
    class _addPostExpression_$Member_$long_$ExpressionType {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                postExpressionService.addPostExpression(member, postId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);

        }

        @Test
        void given_noExistPost_then_throwErrorCase400021() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                postExpressionService.addPostExpression(member, postId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_POST);
        }

        @Test
        void given_alreadyAddExpressionBefore_then_throwErrorCase400028() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            Post post = post();
            ExpressionType expressionType = ExpressionType.LIKE;
            PostExpression postExpression = postExpression(expressionType, member);
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(postExpressionRepository.findByMemberAndPost(post, member)).willReturn(Optional.of(postExpression));

            // when
            Exception exception = catchException(() -> {
                postExpressionService.addPostExpression(member, postId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            then(postExpressionRepository).should(times(1)).findByMemberAndPost(post, member);
            then(postExpressionRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_ALREADY_EXIST_EXPRESSION);

        }

        @Test
        void given_invoke_then_invokeSave() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            Post post = post();
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(postExpressionRepository.findByMemberAndPost(post, member)).willReturn(Optional.empty());

            // when
            postExpressionService.addPostExpression(member, postId, expressionType);

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            then(postExpressionRepository).should(times(1)).findByMemberAndPost(post, member);
            then(postExpressionRepository).should(times(1)).save(any(PostExpression.class));
            then(postExpressionRepository).shouldHaveNoMoreInteractions();

        }


        Post post() {
            return Post.builder()
                .postId(1L)
                .likes(1)
                .hates(1)
                .build();
        }

        PostExpression postExpression(ExpressionType expressionType, Member member) {
            return PostExpression.builder()
                .expressionType(expressionType)
                .member(member)
                .build();
        }

    }

    @Nested
    class _removePostExpression_$Member_$long_$ExpressionType {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                postExpressionService.removePostExpression(member, postId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);

        }

        @Test
        void given_noExistPost_then_throwErrorCase400021() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                postExpressionService.removePostExpression(member, postId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_POST);
        }

        @Test
        void given_noExistPostExpression_then_throwErrorCase400025() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            Post post = post();
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(postExpressionRepository.findByMemberAndPost(post, member)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                postExpressionService.removePostExpression(member, postId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            then(postExpressionRepository).should(times(1)).findByMemberAndPost(post, member);
            then(postExpressionRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NOT_REGISTERED_BEFORE);

        }

        @Test
        void given_noEqualsPostExpression_then_throwErrorCase400025() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            Post post = post();
            PostExpression postExpression = postExpression(ExpressionType.HATE, member);
            ExpressionType expressionType = ExpressionType.LIKE;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(postExpressionRepository.findByMemberAndPost(post, member)).willReturn(Optional.of(postExpression));

            // when
            Exception exception = catchException(() -> {
                postExpressionService.removePostExpression(member, postId, expressionType);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            then(postExpressionRepository).should(times(1)).findByMemberAndPost(post, member);
            then(postExpressionRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NOT_REGISTERED_BEFORE);

        }

        @Test
        void given_invoke_then_invokeRemove() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            Post post = post();
            ExpressionType expressionType = ExpressionType.LIKE;
            PostExpression postExpression = postExpression(expressionType, member);
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(postExpressionRepository.findByMemberAndPost(post, member)).willReturn(Optional.of(postExpression));

            // when
            postExpressionService.removePostExpression(member, postId, expressionType);

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            then(postExpressionRepository).should(times(1)).findByMemberAndPost(post, member);
            then(postExpressionRepository).shouldHaveNoMoreInteractions();
            then(entityManager).should(times(1)).remove(postExpression);
            then(entityManager).shouldHaveNoMoreInteractions();

        }


        Post post() {
            return Post.builder()
                .postId(1L)
                .likes(1)
                .hates(1)
                .build();
        }

        PostExpression postExpression(ExpressionType expressionType, Member member) {
            return PostExpression.builder()
                .expressionType(expressionType)
                .member(member)
                .build();
        }
    }

}
