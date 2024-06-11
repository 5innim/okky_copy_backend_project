//package com.innim.okkycopy.domain.board.event;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.then;
//import static org.mockito.Mockito.times;
//
//import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
//import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
//import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailsResponse;
//import com.innim.okkycopy.global.auth.CustomUserDetails;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//@ExtendWith(MockitoExtension.class)
//public class EventControllerTest {
//
//    @Mock
//    EventPostService eventPostService;
//    @InjectMocks
//    EventController eventController;
//
//    @Test
//    void writeEventPostTest() {
//        // given
//        PostRequest postRequest = postRequest();
//        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
//
//        // when
//        ResponseEntity response = eventController.eventPostAdd(postRequest, customUserDetails);
//
//        // then
//        then(eventPostService).should(times(1)).addEventPost(postRequest, customUserDetails);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    }
//
//    @Test
//    void getEventPostTest() {
//        // given
//        long id = 1L;
//        PostDetailsResponse postDetailsResponse = postDetailRequest();
//        given(eventPostService.findEventPost(null, id)).willReturn(postDetailsResponse);
//
//        // when
//        ResponseEntity response = eventController.eventPostDetails(null, id);
//
//        // then
//        assertThat(response.getBody()).isEqualTo(postDetailsResponse);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
//
//    @Test
//    void editEventPostTest() {
//        // given
//        long id = 1L;
//        PostRequest postRequest = postRequest();
//        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
//
//        // when
//        ResponseEntity response = eventController.eventPostModify(customUserDetails, postRequest, id);
//
//        // then
//        then(eventPostService).should(times(1)).modifyEventPost(customUserDetails, postRequest, id);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//    }
//
//    @Test
//    void deleteEventPostTest() {
//        // given
//        long id = 1L;
//        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
//
//        // when
//        ResponseEntity response = eventController.eventPostRemove(customUserDetails, id);
//
//        // then
//        then(eventPostService).should(times(1)).removeEventPost(customUserDetails, id);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//    }
//
//    @Test
//    void getBriefPostsTest() {
//        // given
//        long topicId = 1;
//        String keyword = "test_keyword";
//        Pageable pageable = null;
//        given(eventPostService.findEventPostsByTopicIdAndKeyword(topicId, keyword, pageable)).willReturn(null);
//
//        // when
//        ResponseEntity<Object> response = eventController.briefPostList(topicId, keyword, pageable);
//
//        // then
//        then(eventPostService).should(times(1)).findEventPostsByTopicIdAndKeyword(topicId, keyword, pageable);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
//
//    PostRequest postRequest() {
//        return PostRequest.builder()
//            .title("test_title")
//            .topic("test_topic")
//            .tags(Arrays.asList())
//            .content("test_content")
//            .build();
//    }
//
//    PostDetailsResponse postDetailRequest() {
//        return PostDetailsResponse.builder()
//            .title("test_title")
//            .tags(Arrays.asList())
//            .content("test_content")
//            .createdDate(LocalDateTime.now())
//            .build();
//    }
//
//}
