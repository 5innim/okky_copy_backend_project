package com.innim.okkycopy.domain.board.knowledge;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.dto.request.WriteCommentRequest;
import com.innim.okkycopy.domain.board.dto.request.WriteReCommentRequest;
import com.innim.okkycopy.domain.board.dto.request.write.WriteRequest;
import com.innim.okkycopy.domain.board.dto.response.comments.CommentsResponse;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailResponse;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class KnowledgeControllerTest {

    @Mock
    KnowledgeService service;
    @InjectMocks
    KnowledgeController controller;

    @Test
    void writeKnowledgePostTest() {
        // given
        WriteRequest writeRequest = writeRequest();
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

        // when
        ResponseEntity response = controller.writeKnowledgePost(writeRequest, customUserDetails);

        // then
        then(service).should(times(1)).saveKnowledgePost(writeRequest, customUserDetails);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void getKnowledgePostTest() {
        // given
        long id = 1l;
        PostDetailResponse postDetailResponse = postDetailRequest();
        given(service.selectKnowledgePost(id)).willReturn(postDetailResponse);

        // when
        ResponseEntity response = controller.getKnowledgePost(id);

        // then
        assertThat(response.getBody()).isEqualTo(postDetailResponse);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void editKnowledgePostTest() {
        // given
        long id = 1l;
        WriteRequest writeRequest = writeRequest();
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

        // when
        ResponseEntity response = controller.editKnowledgePost(customUserDetails, writeRequest, id);

        // then
        then(service).should(times(1)).updateKnowledgePost(customUserDetails, writeRequest, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteKnowledgePostTest() {
        // given
        long id = 1l;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

        // when
        ResponseEntity response = controller.deleteKnowledgePost(customUserDetails, id);

        // then
        then(service).should(times(1)).deleteKnowledgePost(customUserDetails, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void writeKnowledgeCommentTest() {
        // given
        long id = 1l;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        WriteCommentRequest writeCommentRequest = commentRequest();

        // when
        ResponseEntity response = controller.writeKnowledgeComment(customUserDetails,
            writeCommentRequest, id);

        // then
        then(service).should(times(1)).saveKnowledgeComment(customUserDetails, writeCommentRequest, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void editKnowledgeCommentTest() {
        // given
        long id = 1l;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        WriteCommentRequest writeCommentRequest = commentRequest();

        // when
        ResponseEntity response = controller.editKnowledgeComment(customUserDetails,
            writeCommentRequest, id);

        // then
        then(service).should(times(1)).updateKnowledgeComment(customUserDetails,
            writeCommentRequest, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteKnowledgeCommentTest() {
        // given
        long id = 1l;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

        // when
        ResponseEntity response = controller.deleteKnowledgeComment(customUserDetails, id);

        // then
        then(service).should(times(1)).deleteKnowledgeComment(customUserDetails, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getKnowledgePostComments() {
        // given
        long id = 1l;
        given(service.selectKnowledgeComments(id)).willReturn(new CommentsResponse(Arrays.asList()));

        // when
        ResponseEntity response = controller.getKnowledgePostComments(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void writeKnowledgeReComment() {
        // given
        long postId = 1l;
        long commentId = 1l;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        WriteReCommentRequest writeReCommentRequest = WriteReCommentRequest.builder()
            .mentionId(1l)
            .content("test")
            .build();

        // when
        ResponseEntity response = controller.writeKnowledgeReComment(
            customUserDetails,
            postId,
            commentId,
            writeReCommentRequest
        );

        // then
        then(service).should(times(1)).saveKnowledgeReComment(
            customUserDetails,
            postId,
            commentId,
            writeReCommentRequest
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    WriteRequest writeRequest() {
        return WriteRequest.builder()
            .title("test_title")
            .topic("test_topic")
            .tags(Arrays.asList())
            .content("test_content")
            .build();
    }

    PostDetailResponse postDetailRequest() {
        return PostDetailResponse.builder()
            .title("test_title")
            .tags(Arrays.asList())
            .content("test_content")
            .createdDate(LocalDateTime.now())
            .build();
    }

    WriteCommentRequest commentRequest() {
        return new WriteCommentRequest("test comment");
    }
}