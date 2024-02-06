package com.innim.okkycopy.domain.board.knowledge;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.dto.request.CommentRequest;
import com.innim.okkycopy.domain.board.dto.request.write.WriteRequest;
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
    void writeKnowledgePostCommentTest() {
        // given
        long id = 1l;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        CommentRequest commentRequest = commentRequest();

        // when
        ResponseEntity response = controller.writeKnowledgePostComment(customUserDetails, commentRequest, id);

        // then
        then(service).should(times(1)).saveKnowledgeComment(customUserDetails, commentRequest, id);
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

    CommentRequest commentRequest() {
        return new CommentRequest("test comment");
    }
}