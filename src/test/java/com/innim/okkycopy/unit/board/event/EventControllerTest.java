package com.innim.okkycopy.unit.board.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.response.post.brief.PostListResponse;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailsResponse;
import com.innim.okkycopy.domain.board.event.EventController;
import com.innim.okkycopy.domain.board.event.EventPostService;
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
public class EventControllerTest {

    @Mock
    EventPostService eventPostService;
    @InjectMocks
    EventController eventController;

    @Nested
    class _eventPostAdd_$PostRequest_$CustomUserDetails {

        @Test
        void given_request_then_invokeAddEventPost() {
            // given
            PostRequest postRequest = null;
            CustomUserDetails customUserDetails = null;

            // when
            ResponseEntity<Object> response = eventController.eventPostAdd(postRequest, customUserDetails);

            // then
            then(eventPostService).should(times(1)).addEventPost(postRequest, customUserDetails);
            then(eventPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Nested
    class _eventPostDetails_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeFindEventPost() {
            // given
            CustomUserDetails customUserDetails = null;
            long id = 1L;
            PostDetailsResponse postDetailsResponse = PostDetailsResponse.builder().build();
            given(eventPostService.findEventPost(customUserDetails, id)).willReturn(postDetailsResponse);

            // when
            ResponseEntity<PostDetailsResponse> response = eventController.eventPostDetails(customUserDetails,
                id);

            // then
            then(eventPostService).should(times(1)).findEventPost(customUserDetails, id);
            then(eventPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getBody()).isEqualTo(postDetailsResponse);
        }
    }

    @Nested
    class _eventPostModify_$CustomUserDetails_$PostRequest_$long {

        @Test
        void given_request_then_invokeModifyEventPost() {
            // given
            CustomUserDetails customUserDetails = null;
            PostRequest postRequest = null;
            long id = 1L;

            // when
            ResponseEntity<Object> response = eventController.eventPostModify(customUserDetails, postRequest,
                id);

            // then
            then(eventPostService).should(times(1)).modifyEventPost(customUserDetails, postRequest, id);
            then(eventPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class _eventPostRemove_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeRemoveEventPost() {
            // given
            CustomUserDetails customUserDetails = null;
            long id = 1L;

            // when
            ResponseEntity<Object> response = eventController.eventPostRemove(customUserDetails, id);

            // then
            then(eventPostService).should(times(1)).removeEventPost(customUserDetails, id);
            then(eventPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        }
    }

    @Nested
    class _briefPostList_$Long_$String_$Pageable {

        @Test
        void given_request_then_invokeFindEventPostsByKeywordAndPageable() {
            // given
            Long topicId = 1L;
            String keyword = "";
            Pageable pageable = null;
            PostListResponse postListResponse = PostListResponse.builder().build();
            given(eventPostService.findEventPostsByTopicIdAndKeyword(topicId, keyword, pageable)).willReturn(
                postListResponse);

            // when
            ResponseEntity<PostListResponse> response = eventController.briefPostList(topicId, keyword, pageable);

            // then
            then(eventPostService).should(times(1)).findEventPostsByTopicIdAndKeyword(topicId, keyword, pageable);
            then(eventPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getBody()).isEqualTo(postListResponse);

        }
    }


}
