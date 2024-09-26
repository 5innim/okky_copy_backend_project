package com.innim.okkycopy.integration.board.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.google.gson.Gson;
import com.innim.okkycopy.common.annotation.WithMockCustomUser;
import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.request.write.TagInfo;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("/board/event/posts")
public class _board_event_posts {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @BeforeAll
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    @Transactional
    @WithMockCustomUser
    void given_request_then_response200() throws Exception {
        // given
        mockMvc.perform(
            MockMvcRequestBuilders.post("/board/event/write")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest()))
        );

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/board/event/posts?topicId=10&page=0&size=20&sort=likes,desc&sort=createdDate,desc")
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    PostRequest postRequest() {
        PostRequest request = new PostRequest();
        request.setTitle("test_title");
        request.setTopic("IT행사");
        request.setTags(Arrays.asList(new TagInfo("tag1"), new TagInfo("tag2")));
        request.setContent("test_content");

        return request;
    }

}
