package com.innim.okkycopy.integration.board;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.innim.okkycopy.common.annotation.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class TopicsTest {

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
    @WithMockCustomUser
    void topicsTest() throws Exception {
        // given
        // init_test_data procedure success

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/board/topics"));

        // then
        resultActions.andExpect(jsonPath("types").exists());
    }


}
