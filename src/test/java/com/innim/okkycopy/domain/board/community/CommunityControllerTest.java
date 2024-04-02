package com.innim.okkycopy.domain.board.community;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailsResponse;
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
public class CommunityControllerTest {

    @Mock
    CommunityPostService communityPostService;
    @InjectMocks
    CommunityController communityController;

    @Test
    void writeKnowledgePostTest() {
        // given
        PostRequest postRequest = postRequest();
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

        // when
        ResponseEntity response = communityController.communityPostAdd(postRequest, customUserDetails);

        // then
        then(communityPostService).should(times(1)).addCommunityPost(postRequest, customUserDetails);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void getKnowledgePostTest() {
        // given
        long id = 1L;
        PostDetailsResponse postDetailsResponse = postDetailRequest();
        given(communityPostService.findCommunityPost(null, id)).willReturn(postDetailsResponse);

        // when
        ResponseEntity response = communityController.communityPostDetails(null, id);

        // then
        assertThat(response.getBody()).isEqualTo(postDetailsResponse);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    PostRequest postRequest() {
        return PostRequest.builder()
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
