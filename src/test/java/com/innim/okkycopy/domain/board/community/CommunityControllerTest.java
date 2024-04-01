package com.innim.okkycopy.domain.board.community;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
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
public class CommunityControllerTest {

    @Mock
    CommunityPostService communityPostService;
    @InjectMocks
    CommunityController communityController;

    @Test
    void writeKnowledgePostTest() {
        // given
        PostRequest postRequest = writeRequest();
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

        // when
        ResponseEntity response = communityController.communityPostAdd(postRequest, customUserDetails);

        // then
        then(communityPostService).should(times(1)).addCommunityPost(postRequest, customUserDetails);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    PostRequest writeRequest() {
        return PostRequest.builder()
            .title("test_title")
            .topic("test_topic")
            .tags(Arrays.asList())
            .content("test_content")
            .build();
    }

}
