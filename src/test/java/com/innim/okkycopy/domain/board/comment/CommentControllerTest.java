package com.innim.okkycopy.domain.board.comment;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.dto.request.WriteCommentRequest;
import com.innim.okkycopy.domain.board.dto.request.WriteReCommentRequest;
import com.innim.okkycopy.domain.board.dto.response.comments.CommentsResponse;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    CommentService commentService;
    @InjectMocks
    CommentController commentController;

    @Test
    void writeCommentTest() {
        // given
        long id = 1l;
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
        long id = 1l;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        WriteCommentRequest writeCommentRequest = commentRequest();

        // when
        ResponseEntity response = commentController.editComment(customUserDetails,
            writeCommentRequest, id);

        // then
        then(commentService).should(times(1)).updateKnowledgeComment(customUserDetails,
            writeCommentRequest, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteCommentTest() {
        // given
        long id = 1l;
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
        long id = 1l;
        given(commentService.selectKnowledgeComments(id)).willReturn(new CommentsResponse(Arrays.asList()));

        // when
        ResponseEntity response = commentController.getComments(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void writeReComment() {
        // given
        long postId = 1l;
        long commentId = 1l;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        WriteReCommentRequest writeReCommentRequest = WriteReCommentRequest.builder()
            .mentionId(1l)
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

    WriteCommentRequest commentRequest() {
        return new WriteCommentRequest("test comment");
    }
}
