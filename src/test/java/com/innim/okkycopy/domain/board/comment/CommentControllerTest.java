package com.innim.okkycopy.domain.board.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.comment.dto.request.CommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.request.ReCommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentListResponse;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import java.util.Arrays;
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

    @Test
    void writeCommentTest() {
        // given
        long id = 1L;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        CommentRequest commentRequest = commentRequest();

        // when
        ResponseEntity response = commentController.commentAdd(customUserDetails,
            commentRequest, id);

        // then
        then(commentCrudService).should(times(1)).addComment(customUserDetails, commentRequest, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void editCommentTest() {
        // given
        long id = 1L;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        CommentRequest commentRequest = commentRequest();

        // when
        ResponseEntity response = commentController.commentModify(customUserDetails,
            commentRequest, id);

        // then
        then(commentCrudService).should(times(1)).modifyComment(customUserDetails,
            commentRequest, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteCommentTest() {
        // given
        long id = 1L;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

        // when
        ResponseEntity response = commentController.commentRemove(customUserDetails, id);

        // then
        then(commentCrudService).should(times(1)).removeComment(customUserDetails, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getComments() {
        // given
        long id = 1L;
        given(commentCrudService.findComments(null, id)).willReturn(new CommentListResponse(Arrays.asList()));

        // when
        ResponseEntity response = commentController.commentList(null, id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void writeReComment() {
        // given
        long postId = 1L;
        long commentId = 1L;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        ReCommentRequest reCommentRequest = ReCommentRequest.builder()
            .mentionId(1L)
            .content("test")
            .build();

        // when
        ResponseEntity response = commentController.reCommentAdd(
            customUserDetails,
            postId,
            commentId,
            reCommentRequest
        );

        // then
        then(commentCrudService).should(times(1)).addReComment(
            customUserDetails,
            postId,
            commentId,
            reCommentRequest
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

//    @Test
//    void getReCommentsTest() {
//        // given
//        long id = 1L;
//        given(commentCrudService.findReComments(null, id)).willReturn(new CommentListResponse(Arrays.asList()));
//
//        // when
//        ResponseEntity response = commentController.reCommentList(null, id);
//
//        // then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    @Test
    void makeLikeExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = commentController.likeExpressionAdd(customUserDetails, id);

        // then
        then(commentExpressionService).should(times(1))
            .addCommentExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void makeHateExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = commentController.hateExpressionAdd(customUserDetails, id);

        // then
        then(commentExpressionService).should(times(1))
            .addCommentExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void removeLikeExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = commentController.likeExpressionRemove(customUserDetails, id);

        // then
        then(commentExpressionService).should(times(1))
            .removeCommentExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void removeHateExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = commentController.hateExpressionRemove(customUserDetails, id);

        // then
        then(commentExpressionService).should(times(1))
            .removeCommentExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    CommentRequest commentRequest() {
        return new CommentRequest("test comment");
    }
}
