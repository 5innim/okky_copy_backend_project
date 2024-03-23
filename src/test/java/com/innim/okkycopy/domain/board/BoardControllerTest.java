package com.innim.okkycopy.domain.board;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.*;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.dto.request.ScrapRequest;
import com.innim.okkycopy.domain.board.dto.response.topics.TopicResponse;
import com.innim.okkycopy.domain.board.dto.response.topics.TopicsResponse;
import com.innim.okkycopy.domain.board.dto.response.topics.TypeResponse;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.member.entity.Member;

import java.io.IOException;
import java.util.Arrays;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.commons.S3Uploader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BoardControllerTest {
    @Mock
    BoardService boardService;
    @Mock
    S3Uploader s3Uploader;
    @InjectMocks
    BoardController boardController;

    @Test
    void serveTopicsTest() throws Exception {
        // given
        TopicsResponse topicsResponse = topicsResponse();
        given(boardService.findAllBoardTopics()).willReturn(topicsResponse);

        // when
        ResponseEntity response =  boardController.serveTopics();

        // then
        then(boardService).should(times(1)).findAllBoardTopics();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(TopicsResponse.class);
    }

    @Test
    void doScrapTest() throws Exception {
        // given
        ScrapRequest request = scrapRequest();

        // when
        boardController.doScrap(request, WithMockCustomUserSecurityContextFactory.customUserDetailsMock());

        // then
        then(boardService).should(times(1)).scrapPost(any(Member.class), anyLong());

    }

    @Test
    void cancelScrapTest() throws Exception {
        // given
        ScrapRequest request = scrapRequest();

        // when
        boardController.cancelScrap(request, WithMockCustomUserSecurityContextFactory.customUserDetailsMock());

        // then
        then(boardService).should(times(1)).cancelScrap(any(Member.class), anyLong());
    }

    @Test
    void makeLikeExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = boardController.makeLikeExpression(customUserDetails, id);

        // then
        then(boardService).should(times(1))
                .insertPostExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void makeHateExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = boardController.makeHateExpression(customUserDetails, id);

        // then
        then(boardService).should(times(1))
                .insertPostExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void removeLikeExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = boardController.removeLikeExpression(customUserDetails, id);

        // then
        then(boardService).should(times(1))
                .deletePostExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void removeHateExpressionTest() {
        // given
        CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        long id = 1L;

        // when
        ResponseEntity<Object> response = boardController.removeHateExpression(customUserDetails, id);

        // then
        then(boardService).should(times(1))
                .deletePostExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void saveFileTest() throws IOException {
        // given
        MultipartFile file = null;

        // when
        boardController.saveFile(file);

        // then
        then(s3Uploader).should(times(1)).uploadFileToS3(file);
    }


    private TopicsResponse topicsResponse() {
        TopicResponse tr1 = new TopicResponse("topic1", 1);
        TopicResponse tr2 = new TopicResponse("topic2", 2);
        TopicResponse tr3 = new TopicResponse("topic3", 3);

        TypeResponse typeResponse1 = new TypeResponse("type1", 1, Arrays.asList(tr1, tr2));
        TypeResponse typeResponse2 = new TypeResponse("type2", 2, Arrays.asList(tr3));

        return new TopicsResponse(Arrays.asList(typeResponse1, typeResponse2));
    }

    private ScrapRequest scrapRequest() {
        return new ScrapRequest(1l);
    }

}

