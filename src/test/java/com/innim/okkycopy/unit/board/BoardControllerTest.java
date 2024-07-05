package com.innim.okkycopy.unit.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.BoardController;
import com.innim.okkycopy.domain.board.dto.response.FileResponse;
import com.innim.okkycopy.domain.board.dto.response.topics.TopicListResponse;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.service.PostExpressionService;
import com.innim.okkycopy.domain.board.service.PostScrapService;
import com.innim.okkycopy.domain.board.service.BoardTopicService;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.common.S3Uploader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {

    @Mock
    PostExpressionService postExpressionService;
    @Mock
    PostScrapService postScrapService;
    @Mock
    BoardTopicService boardTopicService;
    @Mock
    S3Uploader s3Uploader;
    @InjectMocks
    BoardController boardController;

    @Nested
    class _boardTopicList {

        @Test
        void given_request_then_responseTopicListResponse() {
            // given
            TopicListResponse topicListResponse = topicListResponse();
            given(boardTopicService.findBoardTopics()).willReturn(topicListResponse);

            // when
            ResponseEntity<TopicListResponse> response = boardController.boardTopicList();

            // then
            assertThat(response.getBody()).isEqualTo(topicListResponse);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        }

        TopicListResponse topicListResponse() {
            return new TopicListResponse(Collections.emptyList());
        }
    }

    @Nested
    class _scrapAdd_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeAddScrap() {
            // given
            long id = 1L;
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

            // when
            ResponseEntity<Object> response = boardController.scrapAdd(customUserDetails, id);

            // then
            then(postScrapService).should(times(1)).addScrap(customUserDetails.getMember(), id);
            then(postScrapService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

    }

    @Nested
    class _scrapRemove_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeRemoveScrap() {
            // given
            long id = 1L;
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

            // when
            ResponseEntity<Object> response = boardController.scrapRemove(customUserDetails, id);

            // then
            then(postScrapService).should(times(1)).removeScrap(customUserDetails.getMember(), id);
            then(postScrapService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

    }

    @Nested
    class _likeExpressionAdd_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeAddPostExpression() {
            // given
            long id = 1L;
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

            // when
            ResponseEntity<Object> response = boardController.likeExpressionAdd(customUserDetails, id);

            // then
            then(postExpressionService).should(times(1))
                .addPostExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
            then(postExpressionService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        }

    }

    @Nested
    class _hateExpressionAdd_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeAddPostExpression() {
            // given
            long id = 1L;
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

            // when
            ResponseEntity<Object> response = boardController.hateExpressionAdd(customUserDetails, id);

            // then
            then(postExpressionService).should(times(1))
                .addPostExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
            then(postExpressionService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Nested
    class _likeExpressionRemove_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeRemovePostExpression() {
            // given
            long id = 1L;
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

            // when
            ResponseEntity<Object> response = boardController.likeExpressionRemove(customUserDetails, id);

            // then
            then(postExpressionService).should(times(1))
                .removePostExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
            then(postExpressionService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class _hateExpressionRemove_$CustomUserDetails_$long {

        @Test
        void given_request_then_invokeRemovePostExpression() {
            // given
            long id = 1L;
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

            // when
            ResponseEntity<Object> response = boardController.hateExpressionRemove(customUserDetails, id);

            // then
            then(postExpressionService).should(times(1))
                .removePostExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
            then(postExpressionService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class _fileAdd_$MultipartFile {

        @Test
        void given_request_then_responseFileResponse() throws IOException {
            // given
            String content = "test_content";
            MultipartFile file = new MockMultipartFile("test_file", content.getBytes(StandardCharsets.UTF_8));
            given(s3Uploader.uploadFileToS3(file)).willReturn("test_file_access_url");

            // when
            ResponseEntity<FileResponse> response = boardController.fileAdd(file);

            // then
            then(s3Uploader).should(times(1)).uploadFileToS3(file);
            then(s3Uploader).shouldHaveNoMoreInteractions();
            assertThat(Objects.requireNonNull(response.getBody()).getFileUrl()).isEqualTo("test_file_access_url");

        }
    }

}
