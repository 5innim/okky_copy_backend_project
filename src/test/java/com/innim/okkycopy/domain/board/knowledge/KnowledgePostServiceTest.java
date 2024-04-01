package com.innim.okkycopy.domain.board.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailsResponse;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.knowledge.repository.KnowledgePostRepository;
import com.innim.okkycopy.domain.board.repository.BoardTopicRepository;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class KnowledgePostServiceTest {

    @Mock
    KnowledgePostRepository knowledgePostRepository;
    @Mock
    BoardTopicRepository boardTopicRepository;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    KnowledgePostService knowledgePostService;

    @Nested
    class SelectKnowledgePostTest {

        @Test
        void given_notExistPostId_then_throwStatusCode400Exception() {
            // given
            long notExistPostId = 1L;
            given(knowledgePostRepository.findByPostId(notExistPostId)).willReturn(Optional.empty());

            // when
            Throwable thrown = catchThrowable(() -> {
                knowledgePostService.findKnowledgePost(null, notExistPostId);
            });

            // then
            then(knowledgePostRepository).should(times(1)).findByPostId(notExistPostId);
            assertThat(thrown).isInstanceOf(StatusCode400Exception.class);
        }

        @Test
        void given_postIdWrittenByUnKnownMember_then_returnEmptyMember() {
            // given
            long postId = 1L;
            KnowledgePost knowledgePost = knowledgePost();
            given(knowledgePostRepository.findByPostId(postId)).willReturn(Optional.of(knowledgePost));
            given(memberRepository.findByMemberId(knowledgePost.getMember().getMemberId())).willReturn(
                Optional.empty());

            // when
            PostDetailsResponse postDetailsResponse = knowledgePostService.findKnowledgePost(null, postId);

            // then
            then(knowledgePostRepository).should(times(1)).findByPostId(postId);
            then(memberRepository).should(times(1)).findByMemberId(knowledgePost.getMember().getMemberId());
            assertThat(postDetailsResponse.getWriterInfo()).isEqualTo(null);
        }

        KnowledgePost knowledgePost() {
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock()
                .getMember();
            List<Post> posts = new ArrayList<>();
            member.setPosts(posts);
            return KnowledgePost.of(writeRequest(), boardTopic(), member);
        }

        PostRequest writeRequest() {
            return PostRequest.builder()
                .topic("test_topic")
                .tags(Arrays.asList())
                .title("test_title")
                .content("test_content")
                .build();
        }

        BoardTopic boardTopic() {
            return BoardTopic.builder()
                .knowledgePosts(Arrays.asList())
                .knowledgeTags(Arrays.asList())
                .topicId(1L)
                .name("test_topic")
                .boardType(BoardType.builder()
                    .build())
                .build();
        }
    }

    @Nested
    class SelectKnowledgePostsByCondition {

        @Test
        void given_topicIdAndNotExistTopic_then_throwNoSuchTopicException() {
            // given
            long topicId = 1;
            String keyword = "test_keyword";
            Pageable pageable = null;
            given(boardTopicRepository.findByTopicId(topicId)).willReturn(Optional.empty());

            // when
            Throwable thrown = catchThrowable(() -> {
                knowledgePostService.findKnowledgePostsByKeywordAndPageable(topicId, keyword, pageable);
            });

            // then
            then(boardTopicRepository).should(times(1)).findByTopicId(topicId);
            assertThat(thrown).isInstanceOf(StatusCode400Exception.class);
        }

        @Test
        void given_topicIdOfExistTopicButNotInKnowledgeType_then_throwStatusCode400Exception() {
            // given
            long topicId = 1L;
            String keyword = "test_keyword";
            Pageable pageable = null;
            given(boardTopicRepository.findByTopicId(topicId)).willReturn(Optional.ofNullable(BoardTopic.builder()
                .topicId(topicId)
                .boardType(BoardType.builder()
                    .typeId(1L)
                    .build())
                .build()));

            // when
            Throwable thrown = catchThrowable(() -> {
                knowledgePostService.findKnowledgePostsByKeywordAndPageable(topicId, keyword, pageable);
            });

            // then
            then(boardTopicRepository).should(times(1)).findByTopicId(topicId);
            assertThat(thrown).isInstanceOf(StatusCode400Exception.class);
        }
    }
}