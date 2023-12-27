package com.innim.okkycopy.domain.board;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.innim.okkycopy.common.annotation.WithMockCustomUser;
import com.innim.okkycopy.domain.board.dto.request.ScrapRequest;
import com.innim.okkycopy.domain.board.dto.response.topics.TopicResponse;
import com.innim.okkycopy.domain.board.dto.response.topics.TopicsResponse;
import com.innim.okkycopy.domain.board.dto.response.topics.TypeResponse;
import com.innim.okkycopy.global.error.handler.GlobalExceptionHandler;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
class BoardControllerTest {

    @Mock
    BoardService boardService;
    @InjectMocks
    BoardController boardController;

    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(boardController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }


    @Test
    @WithMockCustomUser
    void serveTopicsTest() throws Exception {
        // given
        TopicsResponse topicsResponse = topicsResponse();
        given(boardService.findAllBoardTopics()).willReturn(topicsResponse);

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/board/topics")
        );

        // then
        then(boardService).should(times(1)).findAllBoardTopics();
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("types", topicsResponse.getTypes()).exists());
    }

    @Test
    @WithMockCustomUser
    void doScrapTest() throws Exception {
        // given
        ScrapRequest request = scrapRequest();

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/post/scrap")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request))
        );

        // then
        then(boardService).should(times(1)).scrapPost(any(), anyLong());

    }

    @Test
    @WithMockCustomUser
    void cancelScrapTest() throws Exception {
        // given
        ScrapRequest request = scrapRequest();

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/board/post/scrap")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request))
        );

        // then
        then(boardService).should(times(1)).cancelScrap(any(), anyLong());
    }


    private TopicsResponse topicsResponse() {
        TopicResponse tr1 = new TopicResponse("topic1");
        TopicResponse tr2 = new TopicResponse("topic2");
        TopicResponse tr3 = new TopicResponse("topic3");

        TypeResponse typeResponse1 = new TypeResponse(Arrays.asList(tr1, tr2));
        TypeResponse typeResponse2 = new TypeResponse(Arrays.asList(tr3));

        return new TopicsResponse(Arrays.asList(typeResponse1, typeResponse2));
    }

    private ScrapRequest scrapRequest() {
        return new ScrapRequest(1l);
    }

}

