package com.innim.okkycopy.unit.board.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.response.post.PostListResponse;
import com.innim.okkycopy.domain.board.dto.response.post.PostDetailsResponse;
import com.innim.okkycopy.domain.board.knowledge.KnowledgeController;
import com.innim.okkycopy.domain.board.knowledge.KnowledgePostService;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class KnowledgeControllerTest {

    @Mock
    KnowledgePostService knowledgePostService;
    @InjectMocks
    KnowledgeController knowledgeController;

    @Nested
    class _knowledgePostAdd_$PostRequest_$CustomUserDetails {

        @Test
        void given_request_then_invokeAddKnowledgePost() {
            // given
            PostRequest postRequest = null;
            CustomUserDetails customUserDetails = null;

            // when
            ResponseEntity<Object> response = knowledgeController.knowledgePostAdd(postRequest, customUserDetails);

            // then
            then(knowledgePostService).should(times(1)).addKnowledgePost(postRequest, customUserDetails);
            then(knowledgePostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Nested
    class _knowledgePostDetails_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeFindKnowledgePost() {
            // given
            CustomUserDetails customUserDetails = null;
            long id = 1L;
            PostDetailsResponse postDetailsResponse = PostDetailsResponse.builder().build();
            given(knowledgePostService.findKnowledgePost(customUserDetails, id)).willReturn(postDetailsResponse);

            // when
            ResponseEntity<PostDetailsResponse> response = knowledgeController.knowledgePostDetails(customUserDetails,
                id);

            // then
            then(knowledgePostService).should(times(1)).findKnowledgePost(customUserDetails, id);
            then(knowledgePostService).shouldHaveNoMoreInteractions();
            assertThat(response.getBody()).isEqualTo(postDetailsResponse);
        }
    }

    @Nested
    class _knowledgePostModify_$CustomUserDetails_$PostRequest_$long {

        @Test
        void given_request_then_invokeModifyKnowledgePost() {
            // given
            CustomUserDetails customUserDetails = null;
            PostRequest postRequest = null;
            long id = 1L;

            // when
            ResponseEntity<Object> response = knowledgeController.knowledgePostModify(customUserDetails, postRequest,
                id);

            // then
            then(knowledgePostService).should(times(1)).modifyKnowledgePost(customUserDetails, postRequest, id);
            then(knowledgePostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class _knowledgePostRemove_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeRemoveKnowledgePost() {
            // given
            CustomUserDetails customUserDetails = null;
            long id = 1L;

            // when
            ResponseEntity<Object> response = knowledgeController.knowledgePostRemove(customUserDetails, id);

            // then
            then(knowledgePostService).should(times(1)).removeKnowledgePost(customUserDetails, id);
            then(knowledgePostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        }
    }

    @Nested
    class _briefPostList_$Long_$String_$Pageable {

        @Test
        void given_request_then_invokeFindKnowledgePostsByKeywordAndPageable() {
            // given
            Long topicId = 1L;
            String keyword = "";
            Pageable pageable = null;
            PostListResponse postListResponse = PostListResponse.builder().build();
            given(knowledgePostService.findKnowledgePostsByTopicIdAndKeyword(topicId, keyword, pageable)).willReturn(
                postListResponse);

            // when
            ResponseEntity<PostListResponse> response = knowledgeController.briefPostList(topicId, keyword, pageable);

            // then
            then(knowledgePostService).should(times(1)).findKnowledgePostsByTopicIdAndKeyword(topicId, keyword, pageable);
            then(knowledgePostService).shouldHaveNoMoreInteractions();
            assertThat(response.getBody()).isEqualTo(postListResponse);

        }
    }


}
