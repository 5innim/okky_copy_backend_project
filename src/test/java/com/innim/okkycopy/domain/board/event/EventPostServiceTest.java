package com.innim.okkycopy.domain.board.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.event.entity.EventPost;
import com.innim.okkycopy.domain.board.event.entity.EventTag;
import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.request.write.TagInfo;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailsResponse;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Tag;
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
public class EventPostServiceTest {

    @Mock
    EventPostRepository eventPostRepository;
    @Mock
    BoardTopicRepository boardTopicRepository;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    EventPostService eventPostService;

    @Nested
    class SelectEventPostTest {

        @Test
        void given_notExistPostId_then_throwStatusCode400Exception() {
            // given
            long notExistPostId = 1L;
            given(eventPostRepository.findByPostId(notExistPostId)).willReturn(Optional.empty());

            // when
            Throwable thrown = catchThrowable(() -> {
                eventPostService.findEventPost(null, notExistPostId);
            });

            // then
            then(eventPostRepository).should(times(1)).findByPostId(notExistPostId);
            assertThat(thrown).isInstanceOf(StatusCode400Exception.class);
        }

        @Test
        void given_postIdWrittenByUnKnownMember_then_returnEmptyMember() {
            // given
            long postId = 1L;
            EventPost eventPost = eventPost();
            given(eventPostRepository.findByPostId(postId)).willReturn(Optional.of(eventPost));
            given(memberRepository.findByMemberId(eventPost.getMember().getMemberId())).willReturn(
                Optional.empty());

            // when
            PostDetailsResponse postDetailsResponse = eventPostService.findEventPost(null, postId);

            // then
            then(eventPostRepository).should(times(1)).findByPostId(postId);
            then(memberRepository).should(times(1)).findByMemberId(eventPost.getMember().getMemberId());
            assertThat(postDetailsResponse.getWriterInfo()).isEqualTo(null);
        }

        EventPost eventPost() {
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock()
                .getMember();
            List<Post> posts = new ArrayList<>();
            member.setPosts(posts);
            BoardTopic boardTopic = BoardTopic.builder()
                .eventPosts(Arrays.asList())
                .eventTags(Arrays.asList())
                .topicId(1L)
                .name("test_topic")
                .boardType(BoardType.builder()
                    .build())
                .build();
            EventPost eventPost = EventPost.builder()
                .member(member)
                .content("test_content")
                .title("test_title")
                .lastUpdate(null)
                .boardTopic(boardTopic)
                .likes(0)
                .hates(0)
                .scraps(0)
                .views(0)
                .comments(0)
                .build();

            PostRequest postRequest = postRequest();
            List<Tag> tags = new ArrayList<>();
            for (TagInfo tag : postRequest.getTags()) {
                tags.add(EventTag.of(eventPost, boardTopic, tag.getName()));
            }
            member.getPosts().add((Post) eventPost);
            eventPost.setTags(tags);

            return eventPost;
        }


        PostRequest postRequest() {
            return PostRequest.builder()
                .topic("test_topic")
                .tags(Arrays.asList())
                .title("test_title")
                .content("test_content")
                .build();
        }
    }

    @Nested
    class SelectEventPostsByCondition {

        @Test
        void given_topicIdAndNotExistTopic_then_throwNoSuchTopicException() {
            // given
            long topicId = 1;
            String keyword = "test_keyword";
            Pageable pageable = null;
            given(boardTopicRepository.findByTopicId(topicId)).willReturn(Optional.empty());

            // when
            Throwable thrown = catchThrowable(() -> {
                eventPostService.findEventPostsByKeywordAndPageable(topicId, keyword, pageable);
            });

            // then
            then(boardTopicRepository).should(times(1)).findByTopicId(topicId);
            assertThat(thrown).isInstanceOf(StatusCode400Exception.class);
        }

        @Test
        void given_topicIdOfExistTopicButNotInEventType_then_throwStatusCode400Exception() {
            // given
            long topicId = 1L;
            String keyword = "test_keyword";
            Pageable pageable = null;
            given(boardTopicRepository.findByTopicId(topicId)).willReturn(Optional.ofNullable(BoardTopic.builder()
                .topicId(topicId)
                .boardType(BoardType.builder()
                    .typeId(1L)
                    .name("Q&A")
                    .build())
                .build()));

            // when
            Throwable thrown = catchThrowable(() -> {
                eventPostService.findEventPostsByKeywordAndPageable(topicId, keyword, pageable);
            });

            // then
            then(boardTopicRepository).should(times(1)).findByTopicId(topicId);
            assertThat(thrown).isInstanceOf(StatusCode400Exception.class);
        }
    }
}
