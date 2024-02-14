package com.innim.okkycopy.domain.board.knowledge;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.dto.request.write.WriteRequest;
import com.innim.okkycopy.domain.board.dto.response.comments.CommentsResponse;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailResponse;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgeComment;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.knowledge.repository.KnowledgePostRepository;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.exception.NoSuchPostException;
import java.time.LocalDateTime;
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

@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {
    @Mock
    KnowledgePostRepository knowledgePostRepository;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    KnowledgeService knowledgeService;

    @Nested
    class selectKnowledgePostTest {
        @Test
        void given_notExistPostId_then_throwNoSuchPostException() {
            // given
            long notExistPostId = 1l;
            given(knowledgePostRepository.findByPostId(notExistPostId)).willReturn(Optional.empty());

            // when
            Throwable thrown = catchThrowable(() -> {
               knowledgeService.selectKnowledgePost(notExistPostId);
            });

            // then
            then(knowledgePostRepository).should(times(1)).findByPostId(notExistPostId);
            assertThat(thrown).isInstanceOf(NoSuchPostException.class);
        }

        @Test
        void given_postIdWrittenByUnKnownMember_then_returnEmptyMember() {
            // given
            long postId = 1l;
            KnowledgePost knowledgePost = knowledgePost();
            given(knowledgePostRepository.findByPostId(postId)).willReturn(Optional.of(knowledgePost));
            given(memberRepository.findByMemberId(knowledgePost.getMember().getMemberId())).willReturn(Optional.empty());

            // when
            PostDetailResponse postDetailResponse = knowledgeService.selectKnowledgePost(postId);

            // then
            then(knowledgePostRepository).should(times(1)).findByPostId(postId);
            then(memberRepository).should(times(1)).findByMemberId(knowledgePost.getMember().getMemberId());
            assertThat(postDetailResponse.getWriterInfo().getMemberId()).isEqualTo(0);
        }

        KnowledgePost knowledgePost() {
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock()
                .getMember();
            List<Post> posts = new ArrayList<>();
            member.setPosts(posts);
            return KnowledgePost.createKnowledgePost(writeRequest(), boardTopic(), member);
        }

        WriteRequest writeRequest() {
            return WriteRequest.builder()
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
                .topicId(1l)
                .name("test_topic")
                .boardType(BoardType.builder()
                    .build())
                .build();
        }
    }

    @Nested
    class selectKnowledgeComments {
        @Test
        void given_notExistPostId_then_throwNoSuchPostException() {
            // given
            long notExistPostId = 1l;
            given(knowledgePostRepository.findByPostId(notExistPostId)).willReturn(Optional.empty());

            // when
            Throwable thrown = catchThrowable(() -> {
                knowledgeService.selectKnowledgeComments(notExistPostId);
            });

            // then
            then(knowledgePostRepository).should(times(1)).findByPostId(notExistPostId);
            assertThat(thrown).isInstanceOf(NoSuchPostException.class);
        }

        @Test
        void given_correctInfo_then_returnCommentsResponse() {
            // given
            long existPostId = 1l;
            KnowledgeComment existComment = KnowledgeComment.builder()
                .likes(0l)
                .content("test content")
                .member(WithMockCustomUserSecurityContextFactory.customUserDetailsMock()
                    .getMember())
                .commentId(1l)
                .createdDate(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();

            KnowledgePost existPost = KnowledgePost.builder()
                .commentList(Arrays.asList(existComment))
                .build();

            given(knowledgePostRepository.findByPostId(existPostId)).willReturn(Optional.of(existPost));

            // when
            CommentsResponse response = knowledgeService.selectKnowledgeComments(existPostId);

            // then
            then(knowledgePostRepository).should(times(1)).findByPostId(existPostId);
            assertThat(response.getComments().get(0).getContent()).isEqualTo(existComment.getContent());

        }
    }


}