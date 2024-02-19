package com.innim.okkycopy.integration.board.knowledge;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class GetCommentsTest {
    @Autowired
    WebApplicationContext context;
    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    void given_noExistPost_then_responseErrorCode() throws Exception {
        // given
        long postId = 1000l;

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/board/knowledge/posts/" + postId + "/comments")
        );

        // then
        resultActions.andExpect(jsonPath("code").value(400021));
    }

    @Test
    void given_correctPostId_then_responseCommentsInfo() throws Exception {
        // given
        long postId = 1l;

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.get("/board/knowledge/posts/" + postId + "/comments")
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
