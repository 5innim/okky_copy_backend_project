package com.innim.okkycopy.domain.board.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.comment.dto.request.WriteCommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.request.WriteReCommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentsResponse;
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
    CommentService commentService;
    @InjectMocks
    CommentController commentController;

    @Test
    void writeCommentTest() {
        // given
        long id = 1L;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        WriteCommentRequest writeCommentRequest = commentRequest();

        // when
        ResponseEntity response = commentController.writeComment(customUserDetails,
            writeCommentRequest, id);

        // then
        then(commentService).should(times(1)).saveComment(customUserDetails, writeCommentRequest, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void editCommentTest() {
        // given
        long id = 1L;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        WriteCommentRequest writeCommentRequest = commentRequest();

        // when
        ResponseEntity response = commentController.editComment(customUserDetails,
            writeCommentRequest, id);

        // then
        then(commentService).should(times(1)).updateComment(customUserDetails,
            writeCommentRequest, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteCommentTest() {
        // given
        long id = 1L;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

        // when
        ResponseEntity response = commentController.deleteComment(customUserDetails, id);

        // then
        then(commentService).should(times(1)).deleteComment(customUserDetails, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getComments() {
        // given
        long id = 1L;
        given(commentService.selectComments(null, id)).willReturn(new CommentsResponse(Arrays.asList()));

        // when
        ResponseEntity response = commentController.getComments(null, id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void writeReComment() {
        // given
        long postId = 1L;
        long commentId = 1L;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        WriteReCommentRequest writeReCommentRequest = WriteReCommentRequest.builder()
            .mentionId(1L)
            .content("test")
            .build();

        // when
        ResponseEntity response = commentController.writeReComment(
            customUserDetails,
            postId,
            commentId,
            writeReCommentRequest
        );

        // then
        then(commentService).should(times(1)).saveReComment(
            customUserDetails,
            postId,
            commentId,
            writeReCommentRequest
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void getReCommentsTest() {
        // given
        long id = 1L;
        given(commentService.selectReComments(null, id)).willReturn(new CommentsResponse(Arrays.asList()));

        // when
        ResponseEntity response = commentController.getReComments(null, id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void makeLikeExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = commentController.makeLikeExpression(customUserDetails, id);

        // then
        then(commentService).should(times(1))
            .insertCommentExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void makeHateExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = commentController.makeHateExpression(customUserDetails, id);

        // then
        then(commentService).should(times(1))
            .insertCommentExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void removeLikeExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = commentController.removeLikeExpression(customUserDetails, id);

        // then
        then(commentService).should(times(1))
            .deleteCommentExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void removeHateExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = commentController.removeHateExpression(customUserDetails, id);

        // then
        then(commentService).should(times(1))
            .deleteCommentExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    WriteCommentRequest commentRequest() {
        return new WriteCommentRequest("test comment");
    }
}
