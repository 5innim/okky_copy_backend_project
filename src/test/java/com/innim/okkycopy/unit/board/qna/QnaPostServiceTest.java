package com.innim.okkycopy.unit.board.qna;

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
import com.innim.okkycopy.domain.board.qna.QnaPostService;
import com.innim.okkycopy.domain.board.qna.entity.QnaPost;
import com.innim.okkycopy.domain.board.qna.QnaPostRepository;
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
public class QnaPostServiceTest {

    @Mock
    BoardTopicRepository boardTopicRepository;
    @Mock
    QnaPostRepository qnaPostRepository;
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
    QnaPostService qnaPostService;

    @Nested
    class _addQnaPost_$PostRequest_$CustomUserDetails {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                qnaPostService.addQnaPost(postRequest, customUserDetails);
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
                qnaPostService.addQnaPost(postRequest, customUserDetails);
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
                    .name("not_qna")
                    .build())
                .build()));

            // when
            Exception exception = catchException(() -> {
                qnaPostService.addQnaPost(postRequest, customUserDetails);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(boardTopicRepository).should(times(1)).findByName(postRequest.getTopic());
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(postRequest.getContent(), true);
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
                    .name("Q&A")
                    .build())
                .build()));

            // when
            qnaPostService.addQnaPost(postRequest, customUserDetails);

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(boardTopicRepository).should(times(1)).findByName(postRequest.getTopic());
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(postRequest().getContent(), true);
            then(imageUsageService).shouldHaveNoMoreInteractions();
            then(qnaPostRepository).should(times(1)).save(any(QnaPost.class));
            then(qnaPostRepository).shouldHaveNoMoreInteractions();

        }

        PostRequest postRequest() {
            PostRequest request = new PostRequest();
            request.setTitle("test_title");
            request.setTopic("test_topic");
            request.setTags(Collections.emptyList());
            request.setContent("test_content");

            return request;
        }

    }

    @Nested
    class _findQnaPost_$CustomUserDetails_$long {

        @Test
        void given_noExistPost_then_throwErrorCase400021() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long postId = 1L;
            given(qnaPostRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                qnaPostService.findQnaPost(customUserDetails, postId);
            });

            // then
            then(qnaPostRepository).should(times(1)).findByPostId(postId);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_POST);

        }

        @Test
        void given_noAuthentication_then_notFindExpressionAndScrap() {
            // given
            CustomUserDetails customUserDetails = null;
            long postId = 1L;
            QnaPost post = qnaPost();
            given(qnaPostRepository.findByPostId(postId)).willReturn(Optional.of(post));

            // when
            qnaPostService.findQnaPost(customUserDetails, postId);

            // then
            then(qnaPostRepository).should(times(1)).findByPostId(postId);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
            then(postExpressionRepository).shouldHaveNoInteractions();
            then(scrapRepository).shouldHaveNoInteractions();
            assertThat(post.getViews()).isEqualTo(1);

        }

        @Test
        void given_authentication_then_findExpressionAndScrap() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long postId = 1L;
            QnaPost post = qnaPost();
            given(qnaPostRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(postExpressionRepository.findByMemberAndPost(post, customUserDetails.getMember())).willReturn(
                Optional.empty());
            given(scrapRepository.findByMemberAndPost(post, customUserDetails.getMember())).willReturn(
                Optional.empty());

            // when
            qnaPostService.findQnaPost(customUserDetails, postId);

            // then
            then(qnaPostRepository).should(times(1)).findByPostId(postId);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
            then(postExpressionRepository).should(times(1)).findByMemberAndPost(post, customUserDetails.getMember());
            then(postExpressionRepository).shouldHaveNoMoreInteractions();
            then(scrapRepository).should(times(1)).findByMemberAndPost(post, customUserDetails.getMember());
            then(scrapRepository).shouldHaveNoMoreInteractions();
            assertThat(post.getViews()).isEqualTo(1);

        }

        QnaPost qnaPost() {
            return QnaPost.builder()
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
    class _modifyQnaPost_$CustomUserDetails_$PostRequest_$long {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                qnaPostService.modifyQnaPost(customUserDetails, postRequest, postId);
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
            given(qnaPostRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                qnaPostService.modifyQnaPost(customUserDetails, postRequest, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(qnaPostRepository).should(times(1)).findByPostId(postId);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_POST);

        }

        @Test
        void given_noExistTopic_then_throwErrorCase400020() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            QnaPost post = qnaPost();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(qnaPostRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(boardTopicRepository.findByName(postRequest.getTopic())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                qnaPostService.modifyQnaPost(customUserDetails, postRequest, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(qnaPostRepository).should(times(1)).findByPostId(postId);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
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
            QnaPost post = qnaPost();
            post.setMember(Member.builder()
                .memberId(2L)
                .build());
            BoardTopic boardTopic = boardTopic();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(qnaPostRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(boardTopicRepository.findByName(postRequest.getTopic())).willReturn(Optional.of(boardTopic));

            // when
            Exception exception = catchException(() -> {
                qnaPostService.modifyQnaPost(customUserDetails, postRequest, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(qnaPostRepository).should(times(1)).findByPostId(postId);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
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
            QnaPost post = qnaPost();
            BoardTopic boardTopic = boardTopic();
            boardTopic.setBoardType(
                BoardType.builder()
                    .name("not_qna")
                    .build()
            );
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(qnaPostRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(boardTopicRepository.findByName(postRequest.getTopic())).willReturn(Optional.of(boardTopic));

            // when
            Exception exception = catchException(() -> {
                qnaPostService.modifyQnaPost(customUserDetails, postRequest, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(qnaPostRepository).should(times(1)).findByPostId(postId);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
            then(boardTopicRepository).should(times(1)).findByName(postRequest.getTopic());
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(qnaPost().getContent(), postRequest.getContent());
            then(imageUsageService).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_BAD_FORM_DATA);

        }

        @Test
        void given_invoke_then_invokeUpdate() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            PostRequest postRequest = postRequest();
            QnaPost post = qnaPost();
            BoardTopic boardTopic = boardTopic();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(qnaPostRepository.findByPostId(postId)).willReturn(Optional.of(post));
            given(boardTopicRepository.findByName(postRequest.getTopic())).willReturn(Optional.of(boardTopic));

            // when
            qnaPostService.modifyQnaPost(customUserDetails, postRequest, postId);

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(qnaPostRepository).should(times(1)).findByPostId(postId);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
            then(boardTopicRepository).should(times(1)).findByName(postRequest.getTopic());
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(qnaPost().getContent(), postRequest.getContent());
            then(imageUsageService).shouldHaveNoMoreInteractions();
            assertThat(post.getTitle()).isEqualTo(postRequest.getTitle());
        }

        PostRequest postRequest() {
            PostRequest request = new PostRequest();
            request.setTitle("test_title");
            request.setTopic("test_topic");
            request.setTags(Collections.emptyList());
            request.setContent("test_content");

            return request;
        }

        QnaPost qnaPost() {
            return QnaPost.builder()
                .member(WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember())
                .tags(Collections.emptyList())
                .build();
        }

        BoardTopic boardTopic() {
            return BoardTopic.builder()
                .boardType(BoardType.builder()
                    .name("Q&A")
                    .build())
                .build();
        }
    }

    @Nested
    class _removeQnaPost_$CustomUserDetails_$long {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                qnaPostService.removeQnaPost(customUserDetails, postId);
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
            given(qnaPostRepository.findByPostId(postId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                qnaPostService.removeQnaPost(customUserDetails, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(qnaPostRepository).should(times(1)).findByPostId(postId);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_NO_SUCH_POST);

        }

        @Test
        void given_notMatchedMember_then_throwErrorCase403002() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            QnaPost post = qnaPost();
            post.setMember(Member.builder()
                .memberId(2L)
                .build());
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(qnaPostRepository.findByPostId(postId)).willReturn(Optional.of(post));

            // when
            Exception exception = catchException(() -> {
                qnaPostService.removeQnaPost(customUserDetails, postId);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(qnaPostRepository).should(times(1)).findByPostId(postId);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode403Exception.class);
            assertThat(((StatusCode403Exception) exception).getErrorCase()).isEqualTo(ErrorCase._403_NO_AUTHORITY);

        }

        @Test
        void given_invoke_then_invokeRemove() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            QnaPost post = qnaPost();
            long postId = 1L;
            given(memberRepository.findByMemberId(customUserDetails.getUserId())).willReturn(
                Optional.of(customUserDetails.getMember()));
            given(qnaPostRepository.findByPostId(postId)).willReturn(Optional.of(post));

            // when
            qnaPostService.removeQnaPost(customUserDetails, postId);

            // then
            then(memberRepository).should(times(1)).findByMemberId(customUserDetails.getUserId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(qnaPostRepository).should(times(1)).findByPostId(postId);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
            then(imageUsageService).should(times(1)).modifyImageUsages(post.getContent(), false);
            then(entityManager).should(times(1)).remove(post);
            then(entityManager).shouldHaveNoMoreInteractions();
        }

        QnaPost qnaPost() {
            return QnaPost.builder()
                .member(WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember())
                .tags(Collections.emptyList())
                .build();
        }

    }

    @Nested
    class _findQnaPostsByTopicIdAndKeyword_$Long_$String_$Pageable {

        @Test
        void given_topicIdIsNull_then_invokeFindPageByKeyword() {
            // given
            Long topicId = null;
            String keyword = "";
            Pageable pageable = null;
            Page<QnaPost> page = new MockPage<>();
            given(qnaPostRepository.findPageByKeyword(keyword, pageable)).willReturn(page);

            // when
            qnaPostService.findQnaPostsByTopicIdAndKeyword(topicId, keyword, pageable);

            // then
            then(qnaPostRepository).should(times(1)).findPageByKeyword(keyword, pageable);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();
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
                qnaPostService.findQnaPostsByTopicIdAndKeyword(topicId, keyword, pageable);
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
                    .name("not_qna")
                    .build()
            );
            given(boardTopicRepository.findByTopicId(topicId)).willReturn(Optional.of(topic));

            // when
            Exception exception = catchException(() -> {
                qnaPostService.findQnaPostsByTopicIdAndKeyword(topicId, keyword, pageable);
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
            Page<QnaPost> page = new MockPage<>();
            given(boardTopicRepository.findByTopicId(topicId)).willReturn(Optional.of(topic));
            given(qnaPostRepository.findPageByBoardTopicAndKeyword(topic, keyword, pageable)).willReturn(page);

            // when
            qnaPostService.findQnaPostsByTopicIdAndKeyword(topicId, keyword, pageable);

            // then
            then(boardTopicRepository).should(times(1)).findByTopicId(topicId);
            then(boardTopicRepository).shouldHaveNoMoreInteractions();
            then(qnaPostRepository).should(times(1)).findPageByBoardTopicAndKeyword(topic, keyword, pageable);
            then(qnaPostRepository).shouldHaveNoMoreInteractions();

        }

        BoardTopic boardTopic() {
            return BoardTopic.builder()
                .boardType(BoardType.builder()
                    .name("Q&A")
                    .build())
                .build();
        }

    }

}
