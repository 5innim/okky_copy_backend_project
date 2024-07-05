package com.innim.okkycopy.unit.board.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.comment.CommentController;
import com.innim.okkycopy.domain.board.comment.service.CommentCrudService;
import com.innim.okkycopy.domain.board.comment.service.CommentExpressionService;
import com.innim.okkycopy.domain.board.comment.dto.request.CommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.request.ReCommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentListResponse;
import com.innim.okkycopy.domain.board.comment.dto.response.ReCommentListResponse;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import java.util.Collections;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    CommentCrudService commentCrudService;
    @Mock
    CommentExpressionService commentExpressionService;
    @InjectMocks
    CommentController commentController;

    @Nested
    class _commentAdd_$CustomUserDetails_$CommentRequest_$long {

        @Test
        void given_request_then_invokeAddComment() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            CommentRequest commentRequest = commentRequest();
            long id = 1L;

            // when
            ResponseEntity<Object> response = commentController.commentAdd(customUserDetails, commentRequest, id);

            // then
            then(commentCrudService).should(times(1)).addComment(customUserDetails, commentRequest, id);
            then(commentCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        CommentRequest commentRequest() {
            return new CommentRequest("test_content");
        }


    }

    @Nested
    class _commentModify_$CustomUserDetails_$CommentRequest_$long {

        @Test
        void given_request_then_invokeModifyComment() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            CommentRequest commentRequest = commentRequest();
            long id = 1L;

            // when
            ResponseEntity<Object> response = commentController.commentModify(customUserDetails, commentRequest, id);

            // then
            then(commentCrudService).should(times(1)).modifyComment(customUserDetails, commentRequest, id);
            then(commentCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        CommentRequest commentRequest() {
            return new CommentRequest("test_content");
        }
    }

    @Nested
    class _commentRemove_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeRemoveComment() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long id = 1L;

            // when
            ResponseEntity<Object> response = commentController.commentRemove(customUserDetails, id);

            // then
            then(commentCrudService).should(times(1)).removeComment(customUserDetails, id);
            then(commentCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class _commentList_$CustomUserDetails_$long {

        @Test
        void given_request_then_responseCommentListResponse() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long id = 1L;
            CommentListResponse commentListResponse = commentListResponse();
            given(commentCrudService.findComments(customUserDetails, id)).willReturn(commentListResponse);

            // when
            ResponseEntity<CommentListResponse> response = commentController.commentList(customUserDetails, 1L);

            // then
            then(commentCrudService).should(times(1)).findComments(customUserDetails, id);
            then(commentCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getBody()).isEqualTo(commentListResponse);
        }

        CommentListResponse commentListResponse() {
            return new CommentListResponse(Collections.emptyList());
        }
    }

    @Nested
    class _reCommentAdd_$CustomUserDetails_$long_$long_$ReCommentRequest {

        @Test
        void given_request_then_invokeAddReComment() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long postId = 1L;
            long commentId = 1L;
            ReCommentRequest reCommentRequest = reCommentRequest();

            // when
            ResponseEntity<Object> response = commentController.reCommentAdd(customUserDetails, postId, commentId,
                reCommentRequest);

            // then
            then(commentCrudService).should(times(1))
                .addReComment(customUserDetails, postId, commentId, reCommentRequest);
            then(commentCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        }

        ReCommentRequest reCommentRequest() {
            return ReCommentRequest.builder()
                .mentionId(1L)
                .content("test_content")
                .build();
        }
    }

    @Nested
    class _reCommentList_$CustomUserDetails_$long {

        @Test
        void given_request_then_responseReCommentListResponse() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long id = 1L;
            ReCommentListResponse reCommentListResponse = reCommentListResponse();
            given(commentCrudService.findReComments(customUserDetails, id)).willReturn(reCommentListResponse);

            // when
            ResponseEntity<ReCommentListResponse> response = commentController.reCommentList(customUserDetails, id);

            // then
            then(commentCrudService).should(times(1)).findReComments(customUserDetails, id);
            then(commentCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getBody()).isEqualTo(reCommentListResponse);
        }

        ReCommentListResponse reCommentListResponse() {
            return new ReCommentListResponse(Collections.emptyList());
        }
    }

    @Nested
    class _likeExpressionAdd_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeAddCommentExpression() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long id = 1L;

            // when
            ResponseEntity<Object> response = commentController.likeExpressionAdd(customUserDetails, id);

            // then
            then(commentExpressionService).should(times(1))
                .addCommentExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
            then(commentExpressionService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Nested
    class _hateExpressionAdd_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeAddCommentExpression() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long id = 1L;

            // when
            ResponseEntity<Object> response = commentController.hateExpressionAdd(customUserDetails, id);

            // then
            then(commentExpressionService).should(times(1))
                .addCommentExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
            then(commentExpressionService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Nested
    class _likeExpressionRemove_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeRemoveCommentExpression() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long id = 1L;

            // when
            ResponseEntity<Object> response = commentController.likeExpressionRemove(customUserDetails, id);

            // then
            then(commentExpressionService).should(times(1))
                .removeCommentExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
            then(commentExpressionService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class _hateExpressionRemove_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeRemoveCommentExpression() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            long id = 1L;

            // when
            ResponseEntity<Object> response = commentController.hateExpressionRemove(customUserDetails, id);

            // then
            then(commentExpressionService).should(times(1))
                .removeCommentExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
            then(commentExpressionService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

}
