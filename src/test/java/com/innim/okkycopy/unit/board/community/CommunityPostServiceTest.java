package com.innim.okkycopy.unit.board.community;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.MockPage;
import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.community.CommunityPostService;
import com.innim.okkycopy.domain.board.community.entity.CommunityPost;
import com.innim.okkycopy.domain.board.community.CommunityPostRepository;
import com.innim.okkycopy.domain.board.repository.BoardTopicRepository;
import com.innim.okkycopy.domain.board.repository.PostExpressionRepository;
import com.innim.okkycopy.domain.board.repository.ScrapRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.common.storage.image_usage.ImageUsageService;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCode403Exception;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CommunityPostServiceTest {

    @Mock
    BoardTopicRepository boardTopicRepository;
    @Mock
    CommunityPostRepository communityPostRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ScrapRepository scrapRepository;
    @Mock
    PostExpressionRepository postExpressionRepository;
    @Mock
    EntityManager entityManager;
    @Mock
    ImageUsageService imageUsageService;

    @InjectMocks
    CommunityPostService communityPostService;

    @Nested
    class _addCommunityPost_$PostRequest_$CustomUserDetails {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                communityPostService.addCommunityPost(postRequest, customUserDetails);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);

        }

        @Test
        void given_noExistTopic_then_throwErrorCase400020() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(boardTopicRepository.findByName(postRequest.getTopic())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                communityPostService.addCommunityPost(postRequest, customUserDetails);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(boardTopicRepository).should(times(1)).findByName(postRequest.getTopic());
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_TOPIC);

        }

        @Test
        void given_notSupportedTopic_then_throwErrorCase400031() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(boardTopicRepository.findByName(postRequest.getTopic())).willReturn(Optional.of(BoardTopic.builder()
                .boardType(BoardType.builder()
                    .name("not_community")
                    .build())
                .build()));

            // when
            Exception exception = catchException(() -> {
                communityPostService.addCommunityPost(postRequest, customUserDetails);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(boardTopicRepository).should(times(1)).findByName(postRequest.getTopic());
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(postRequest().getContent(), true);
            then(imageUsageService).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_BAD_FORM_DATA);

        }

        @Test
        void given_invoke_then_invokeSave() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(boardTopicRepository.findByName(postRequest.getTopic())).willReturn(Optional.of(BoardTopic.builder()
                .boardType(BoardType.builder()
                    .name("커뮤니티")
                    .build())
                .build()));

            // when
            communityPostService.addCommunityPost(postRequest, customUserDetails);

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(boardTopicRepository).should(times(1)).findByName(postRequest.getTopic());
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(postRequest.getContent(), true);
            then(imageUsageService).shouldHaveNoMoreInteractions();
            then(communityPostRepository).should(times(1)).save(any(CommunityPost.class));
            then(communityPostRepository).shouldHaveNoMoreInteractions();

        }

        PostRequest postRequest() {
            return PostRequest.builder()
                .title("test_title")
                .topic("test_topic")
                .tags(Collections.emptyList())
                .content("test_content")
                .build();
        }

    }

    @Nested
    class _findCommunityPost_$CustomUserDetails_$long {

        @Test
        void given_noExistPost_then_throwErrorCase400021() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long postId = 1L;
            given(communityPostRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                communityPostService.findCommunityPost(customUserDetails, postId);
            });

            // then
            then(communityPostRepository).should(times(1)).findByPostId(postId);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_POST);

        }

        @Test
        void given_noAuthentication_then_notFindExpressionAndScrap() {
            // given
            CustomUserDetails customUserDetails = null;
            long postId = 1L;
            CommunityPost post = communityPost();
            given(communityPostRepository.findByPostId(postId)).willReturn(Optional.of(post));

            // when
            communityPostService.findCommunityPost(customUserDetails, postId);

            // then
            then(communityPostRepository).should(times(1)).findByPostId(postId);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
            then(postExpressionRepository).shouldHaveNoInteractions();
            then(scrapRepository).shouldHaveNoInteractions();
            assertThat(post.getViews()).isEqualTo(1);

        }

        @Test
        void given_authentication_then_findExpressionAndScrap() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long postId = 1L;
            CommunityPost post = communityPost();
            given(communityPostRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(postExpressionRepository.findByMemberAndPost(post, customUserDetails.getMember())).willReturn(
                Optional.empty());
            given(scrapRepository.findByMemberAndPost(post, customUserDetails.getMember())).willReturn(
                Optional.empty());

            // when
            communityPostService.findCommunityPost(customUserDetails, postId);

            // then
            then(communityPostRepository).should(times(1)).findByPostId(postId);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
            then(postExpressionRepository).should(times(1)).findByMemberAndPost(post, customUserDetails.getMember());
            then(postExpressionRepository).shouldHaveNoMoreInteractions();
            then(scrapRepository).should(times(1)).findByMemberAndPost(post, customUserDetails.getMember());
            then(scrapRepository).shouldHaveNoMoreInteractions();
            assertThat(post.getViews()).isEqualTo(1);

        }

        CommunityPost communityPost() {
            return CommunityPost.builder()
                .member(WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember())
                .views(0)
                .tags(Collections.emptyList())
                .boardTopic(BoardTopic.builder()
                    .name("test_topic")
                    .build())
                .build();
        }
    }

    @Nested
    class _modifyCommunityPost_$CustomUserDetails_$PostRequest_$long {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                communityPostService.modifyCommunityPost(customUserDetails, postRequest, postId);
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
            PostRequest postRequest = postRequest();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(communityPostRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                communityPostService.modifyCommunityPost(customUserDetails, postRequest, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(communityPostRepository).should(times(1)).findByPostId(postId);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_POST);

        }

        @Test
        void given_noExistTopic_then_throwErrorCase400020() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            CommunityPost post = communityPost();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(communityPostRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(boardTopicRepository.findByName(postRequest.getTopic())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                communityPostService.modifyCommunityPost(customUserDetails, postRequest, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(communityPostRepository).should(times(1)).findByPostId(postId);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
            then(boardTopicRepository).should(times(1)).findByName(postRequest.getTopic());
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_TOPIC);

        }

        @Test
        void given_notMatchedMember_then_throwErrorCase403002() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            CommunityPost post = communityPost();
            post.setMember(Member.builder()
                .memberId(2L)
                .build());
            BoardTopic boardTopic = boardTopic();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(communityPostRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(boardTopicRepository.findByName(postRequest.getTopic())).willReturn(Optional.of(boardTopic));

            // when
            Exception exception = catchException(() -> {
                communityPostService.modifyCommunityPost(customUserDetails, postRequest, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(communityPostRepository).should(times(1)).findByPostId(postId);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
            then(boardTopicRepository).should(times(1)).findByName(postRequest.getTopic());
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode403Exception.class);
            assertThat(((StatusCode403Exception) exception).getErrorCase()).isEqualTo(ErrorCase._403_NO_AUTHORITY);

        }

        @Test
        void given_notSupportedTopic_then_throwErrorCase400031() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            CommunityPost post = communityPost();
            BoardTopic boardTopic = boardTopic();
            boardTopic.setBoardType(
                BoardType.builder()
                    .name("not_community")
                    .build()
            );
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(communityPostRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(boardTopicRepository.findByName(postRequest.getTopic())).willReturn(Optional.of(boardTopic));

            // when
            Exception exception = catchException(() -> {
                communityPostService.modifyCommunityPost(customUserDetails, postRequest, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(communityPostRepository).should(times(1)).findByPostId(postId);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
            then(boardTopicRepository).should(times(1)).findByName(postRequest.getTopic());
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(communityPost().getContent(), postRequest.getContent());
            then(imageUsageService).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_BAD_FORM_DATA);

        }

        @Test
        void given_invoke_then_invokeUpdate() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            CommunityPost post = communityPost();
            BoardTopic boardTopic = boardTopic();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(communityPostRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(boardTopicRepository.findByName(postRequest.getTopic())).willReturn(Optional.of(boardTopic));

            // when
            communityPostService.modifyCommunityPost(customUserDetails, postRequest, postId);

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(communityPostRepository).should(times(1)).findByPostId(postId);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
            then(boardTopicRepository).should(times(1)).findByName(postRequest.getTopic());
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(communityPost().getContent(), postRequest.getContent());
            then(imageUsageService).shouldHaveNoMoreInteractions();
            assertThat(post.getTitle()).isEqualTo(postRequest.getTitle());
        }

        PostRequest postRequest() {
            return PostRequest.builder()
                .title("test_title")
                .content("test_content")
                .topic("test_topic")
                .tags(Collections.emptyList())
                .build();
        }

        CommunityPost communityPost() {
            return CommunityPost.builder()
                .member(WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember())
                .tags(Collections.emptyList())
                .build();
        }

        BoardTopic boardTopic() {
            return BoardTopic.builder()
                .boardType(BoardType.builder()
                    .name("커뮤니티")
                    .build())
                .build();
        }
    }

    @Nested
    class _removeCommunityPost_$CustomUserDetails_$long {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                communityPostService.removeCommunityPost(customUserDetails, postId);
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
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(communityPostRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                communityPostService.removeCommunityPost(customUserDetails, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(communityPostRepository).should(times(1)).findByPostId(postId);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_POST);

        }

        @Test
        void given_notMatchedMember_then_throwErrorCase403002() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            CommunityPost post = communityPost();
            post.setMember(Member.builder()
                .memberId(2L)
                .build());
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(communityPostRepository.findByPostId(postId)).willReturn(Optional.of(post));

            // when
            Exception exception = catchException(() -> {
                communityPostService.removeCommunityPost(customUserDetails, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(communityPostRepository).should(times(1)).findByPostId(postId);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode403Exception.class);
            assertThat(((StatusCode403Exception) exception).getErrorCase()).isEqualTo(ErrorCase._403_NO_AUTHORITY);

        }

        @Test
        void given_invoke_then_invokeRemove() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            CommunityPost post = communityPost();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(communityPostRepository.findByPostId(postId)).willReturn(Optional.of(post));

            // when
            communityPostService.removeCommunityPost(customUserDetails, postId);

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(communityPostRepository).should(times(1)).findByPostId(postId);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(post.getContent(), false);
            then(entityManager).should(times(1)).remove(post);
            then(entityManager).shouldHaveNoMoreInteractions();
        }

        CommunityPost communityPost() {
            return CommunityPost.builder()
                .member(WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember())
                .tags(Collections.emptyList())
                .build();
        }

    }

    @Nested
    class _findCommunityPostsByTopicIdAndKeyword_$Long_$String_$Pageable {

        @Test
        void given_topicIdIsNull_then_invokeFindPageByKeyword() {
            // given
            Long topicId = null;
            String keyword = "";
            Pageable pageable = null;
            Page<CommunityPost> page = new MockPage<>();
            given(communityPostRepository.findPageByKeyword(keyword, pageable)).willReturn(page);

            // when
            communityPostService.findCommunityPostsByTopicIdAndKeyword(topicId, keyword, pageable);

            // then
            then(communityPostRepository).should(times(1)).findPageByKeyword(keyword, pageable);
            then(communityPostRepository).shouldHaveNoMoreInteractions();
        }

        @Test
        void given_topicIdIsNotNullAndNoExistTopic_then_throwErrorCase400020() {
            // given
            Long topicId = 1L;
            String keyword = "";
            Pageable pageable = null;
            given(boardTopicRepository.findByTopicId(topicId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                communityPostService.findCommunityPostsByTopicIdAndKeyword(topicId, keyword, pageable);
            });

            // then
            then(boardTopicRepository).should(times(1)).findByTopicId(topicId);
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_TOPIC);

        }

        @Test
        void given_topicIdIsNotNullAndNotSupportedTopic_then_throwErrorCase400026() {
            // given
            Long topicId = 1L;
            String keyword = "";
            Pageable pageable = null;
            BoardTopic topic = boardTopic();
            topic.setBoardType(
                BoardType.builder()
                    .name("not_community")
                    .build()
            );
            given(boardTopicRepository.findByTopicId(topicId)).willReturn(Optional.of(topic));

            // when
            Exception exception = catchException(() -> {
                communityPostService.findCommunityPostsByTopicIdAndKeyword(topicId, keyword, pageable);
            });

            // then
            then(boardTopicRepository).should(times(1)).findByTopicId(topicId);
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NOT_SUPPORTED_CASE);

        }

        @Test
        void given_topicIdIsNotNull_then_invokeFindPageByBoardTopicAndKeyword() {
            // given
            Long topicId = 1L;
            String keyword = "";
            Pageable pageable = null;
            BoardTopic topic = boardTopic();
            Page<CommunityPost> page = new MockPage<>();
            given(boardTopicRepository.findByTopicId(topicId)).willReturn(Optional.of(topic));
            given(communityPostRepository.findPageByBoardTopicAndKeyword(topic, keyword, pageable)).willReturn(page);

            // when
            communityPostService.findCommunityPostsByTopicIdAndKeyword(topicId, keyword, pageable);

            // then
            then(boardTopicRepository).should(times(1)).findByTopicId(topicId);
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            then(communityPostRepository).should(times(1)).findPageByBoardTopicAndKeyword(topic, keyword, pageable);
            then(communityPostRepository).shouldHaveNoMoreInteractions();

        }

        BoardTopic boardTopic() {
            return BoardTopic.builder()
                .boardType(BoardType.builder()
                    .name("커뮤니티")
                    .build())
                .build();
        }

    }

}
