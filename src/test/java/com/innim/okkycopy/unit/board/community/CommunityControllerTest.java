package com.innim.okkycopy.unit.board.community;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.response.post.PostListResponse;
import com.innim.okkycopy.domain.board.dto.response.post.PostDetailsResponse;
import com.innim.okkycopy.domain.board.community.CommunityController;
import com.innim.okkycopy.domain.board.community.CommunityPostService;
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
public class CommunityControllerTest {

    @Mock
    CommunityPostService communityPostService;
    @InjectMocks
    CommunityController communityController;

    @Nested
    class _communityPostAdd_$PostRequest_$CustomUserDetails {

        @Test
        void given_request_then_invokeAddCommunityPost() {
            // given
            PostRequest postRequest = null;
            CustomUserDetails customUserDetails = null;

            // when
            ResponseEntity<Object> response = communityController.communityPostAdd(postRequest, customUserDetails);

            // then
            then(communityPostService).should(times(1)).addCommunityPost(postRequest, customUserDetails);
            then(communityPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Nested
    class _communityPostDetails_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeFindCommunityPost() {
            // given
            CustomUserDetails customUserDetails = null;
            long id = 1L;
            PostDetailsResponse postDetailsResponse = PostDetailsResponse.builder().build();
            given(communityPostService.findCommunityPost(customUserDetails, id)).willReturn(postDetailsResponse);

            // when
            ResponseEntity<PostDetailsResponse> response = communityController.communityPostDetails(customUserDetails,
                id);

            // then
            then(communityPostService).should(times(1)).findCommunityPost(customUserDetails, id);
            then(communityPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getBody()).isEqualTo(postDetailsResponse);
        }
    }

    @Nested
    class _communityPostModify_$CustomUserDetails_$PostRequest_$long {

        @Test
        void given_request_then_invokeModifyCommunityPost() {
            // given
            CustomUserDetails customUserDetails = null;
            PostRequest postRequest = null;
            long id = 1L;

            // when
            ResponseEntity<Object> response = communityController.communityPostModify(customUserDetails, postRequest,
                id);

            // then
            then(communityPostService).should(times(1)).modifyCommunityPost(customUserDetails, postRequest, id);
            then(communityPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class _communityPostRemove_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeRemoveCommunityPost() {
            // given
            CustomUserDetails customUserDetails = null;
            long id = 1L;

            // when
            ResponseEntity<Object> response = communityController.communityPostRemove(customUserDetails, id);

            // then
            then(communityPostService).should(times(1)).removeCommunityPost(customUserDetails, id);
            then(communityPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        }
    }

    @Nested
    class _briefPostList_$Long_$String_$Pageable {

        @Test
        void given_request_then_invokeFindCommunityPostsByKeywordAndPageable() {
            // given
            Long topicId = 1L;
            String keyword = "";
            Pageable pageable = null;
            PostListResponse postListResponse = PostListResponse.builder().build();
            given(communityPostService.findCommunityPostsByTopicIdAndKeyword(topicId, keyword, pageable)).willReturn(
                postListResponse);

            // when
            ResponseEntity<PostListResponse> response = communityController.briefPostList(topicId, keyword, pageable);

            // then
            then(communityPostService).should(times(1)).findCommunityPostsByTopicIdAndKeyword(topicId, keyword, pageable);
            then(communityPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getBody()).isEqualTo(postListResponse);

        }
    }


}
