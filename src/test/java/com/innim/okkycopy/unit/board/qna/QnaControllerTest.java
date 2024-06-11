package com.innim.okkycopy.unit.board.qna;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.response.post.brief.PostListResponse;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailsResponse;
import com.innim.okkycopy.domain.board.qna.QnaController;
import com.innim.okkycopy.domain.board.qna.QnaPostService;
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
public class QnaControllerTest {

    @Mock
    QnaPostService qnaPostService;
    @InjectMocks
    QnaController qnaController;

    @Nested
    class _qnaPostAdd_$PostRequest_$CustomUserDetails {

        @Test
        void given_request_then_invokeAddQnaPost() {
            // given
            PostRequest postRequest = null;
            CustomUserDetails customUserDetails = null;

            // when
            ResponseEntity<Object> response = qnaController.qnaPostAdd(postRequest, customUserDetails);

            // then
            then(qnaPostService).should(times(1)).addQnaPost(postRequest, customUserDetails);
            then(qnaPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Nested
    class _qnaPostDetails_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeFindQnaPost() {
            // given
            CustomUserDetails customUserDetails = null;
            long id = 1L;
            PostDetailsResponse postDetailsResponse = PostDetailsResponse.builder().build();
            given(qnaPostService.findQnaPost(customUserDetails, id)).willReturn(postDetailsResponse);

            // when
            ResponseEntity<PostDetailsResponse> response = qnaController.qnaPostDetails(customUserDetails,
                id);

            // then
            then(qnaPostService).should(times(1)).findQnaPost(customUserDetails, id);
            then(qnaPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getBody()).isEqualTo(postDetailsResponse);
        }
    }

    @Nested
    class _qnaPostModify_$CustomUserDetails_$PostRequest_$long {

        @Test
        void given_request_then_invokeModifyQnaPost() {
            // given
            CustomUserDetails customUserDetails = null;
            PostRequest postRequest = null;
            long id = 1L;

            // when
            ResponseEntity<Object> response = qnaController.qnaPostModify(customUserDetails, postRequest,
                id);

            // then
            then(qnaPostService).should(times(1)).modifyQnaPost(customUserDetails, postRequest, id);
            then(qnaPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class _qnaPostRemove_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeRemoveQnaPost() {
            // given
            CustomUserDetails customUserDetails = null;
            long id = 1L;

            // when
            ResponseEntity<Object> response = qnaController.qnaPostRemove(customUserDetails, id);

            // then
            then(qnaPostService).should(times(1)).removeQnaPost(customUserDetails, id);
            then(qnaPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        }
    }

    @Nested
    class _briefPostList_$Long_$String_$Pageable {

        @Test
        void given_request_then_invokeFindQnaPostsByKeywordAndPageable() {
            // given
            Long topicId = 1L;
            String keyword = "";
            Pageable pageable = null;
            PostListResponse postListResponse = PostListResponse.builder().build();
            given(qnaPostService.findQnaPostsByTopicIdAndKeyword(topicId, keyword, pageable)).willReturn(
                postListResponse);

            // when
            ResponseEntity<PostListResponse> response = qnaController.briefPostList(topicId, keyword, pageable);

            // then
            then(qnaPostService).should(times(1)).findQnaPostsByTopicIdAndKeyword(topicId, keyword, pageable);
            then(qnaPostService).shouldHaveNoMoreInteractions();
            assertThat(response.getBody()).isEqualTo(postListResponse);

        }
    }


}
