package com.innim.okkycopy.unit.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.domain.board.repository.PostRepository;
import com.innim.okkycopy.domain.board.repository.ScrapRepository;
import com.innim.okkycopy.domain.board.service.PostScrapService;
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
public class PostScrapServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    ScrapRepository scrapRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    EntityManager entityManager;
    @InjectMocks
    PostScrapService postScrapService;

    @Nested
    class _addScrap_$Member_$long {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                postScrapService.addScrap(member, postId);
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
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                postScrapService.addScrap(member, postId);
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
        void given_invoke_then_invokeSave() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.of(post()));

            // when
            postScrapService.addScrap(member, postId);

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            then(scrapRepository).should(times(1)).save(any(Scrap.class));
            then(scrapRepository).shouldHaveNoMoreInteractions();

        }

        Post post() {
            return Post.builder()
                .postId(1L)
                .scraps(1)
                .build();
        }
    }

    @Nested
    class _removeScrap_$Member_$long {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                postScrapService.removeScrap(member, postId);
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
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                postScrapService.removeScrap(member, postId);
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
        void given_noExistScrap_then_throwErrorCase400022() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            Post post = post();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(scrapRepository.findByMemberAndPost(post, member)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                postScrapService.removeScrap(member, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            then(scrapRepository).should(times(1)).findByMemberAndPost(post, member);
            then(scrapRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_SCRAP);
        }

        @Test
        void given_invoke_then_invokeRemove() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            long postId = 1L;
            Post post = post();
            Scrap scrap = scrap(member, post);
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(postRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(scrapRepository.findByMemberAndPost(post, member)).willReturn(Optional.of(scrap));

            // when
            postScrapService.removeScrap(member, postId);

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(postRepository).should(times(1)).findByPostId(postId);
            then(postRepository).shouldHaveNoMoreInteractions();
            then(scrapRepository).should(times(1)).findByMemberAndPost(post, member);
            then(scrapRepository).shouldHaveNoMoreInteractions();
            then(entityManager).should(times(1)).remove(scrap);
            then(entityManager).shouldHaveNoMoreInteractions();
        }

        Post post() {
            return Post.builder()
                .postId(1L)
                .scraps(1)
                .build();
        }

        Scrap scrap(Member member, Post post) {
            return Scrap.builder()
                .member(member)
                .scrapId(1L)
                .post(post)
                .build();
        }
    }
}
