package com.innim.okkycopy.integration.board.comment;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
public class GetReCommentsTest {
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
    void given_noExistComment_then_responseErrorCode() throws Exception {
        // given
        long commentId = 1000l;

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/board/comments/" + commentId + "/recomments")
        );

        // then
        resultActions.andExpect(jsonPath("code").value(400023));
    }

    @Test
    void given_correctPostId_then_responseCommentsInfo() throws Exception {
        // given
        long commentId = 1l;

        // when
        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/board/comments/" + commentId + "/recomments")
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
