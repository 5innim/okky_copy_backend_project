package com.innim.okkycopy.domain.board.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.dto.request.write.PostAddRequest;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailsResponse;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
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
        PostAddRequest postAddRequest = writeRequest();
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

        // when
        ResponseEntity response = controller.knowledgePostAdd(postAddRequest, customUserDetails);

        // then
        then(service).should(times(1)).addKnowledgePost(postAddRequest, customUserDetails);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void getKnowledgePostTest() {
        // given
        long id = 1L;
        PostDetailsResponse postDetailsResponse = postDetailRequest();
        given(service.findKnowledgePost(null, id)).willReturn(postDetailsResponse);

        // when
        ResponseEntity response = controller.knowledgePostDetails(null, id);

        // then
        assertThat(response.getBody()).isEqualTo(postDetailsResponse);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void editKnowledgePostTest() {
        // given
        long id = 1L;
        PostAddRequest postAddRequest = writeRequest();
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

        // when
        ResponseEntity response = controller.knowledgePostModify(customUserDetails, postAddRequest, id);

        // then
        then(service).should(times(1)).modifyKnowledgePost(customUserDetails, postAddRequest, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteKnowledgePostTest() {
        // given
        long id = 1L;
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

        // when
        ResponseEntity response = controller.knowledgePostRemove(customUserDetails, id);

        // then
        then(service).should(times(1)).removeKnowledgePost(customUserDetails, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getBriefPostsTest() {
        // given
        long topicId = 1;
        String keyword = "test_keyword";
        Pageable pageable = null;
        given(service.findKnowledgePostsByKeywordAndPageable(topicId, keyword, pageable)).willReturn(null);

        // when
        ResponseEntity<Object> response = controller.briefPostList(topicId, keyword, pageable);

        // then
        then(service).should(times(1)).findKnowledgePostsByKeywordAndPageable(topicId, keyword, pageable);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    PostAddRequest writeRequest() {
        return PostAddRequest.builder()
            .title("test_title")
            .topic("test_topic")
            .tags(Arrays.asList())
            .content("test_content")
            .build();
    }

    PostDetailsResponse postDetailRequest() {
        return PostDetailsResponse.builder()
            .title("test_title")
            .tags(Arrays.asList())
            .content("test_content")
            .createdDate(LocalDateTime.now())
            .build();
    }

}