package com.innim.okkycopy.integration.board.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.google.gson.Gson;
import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.common.annotation.WithMockCustomUser;
import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.request.write.TagInfo;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("/board/knowledge/write")
public class _board_knowledge_write {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MemberRepository memberRepository;

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
    void given_invalidTopic_then_response400() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        postRequest.setTopic("");

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/knowledge/write")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @Transactional
    @WithMockCustomUser
    void given_invalidTitle_then_response400() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        postRequest.setTitle("0123456789012345678901234567890");

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/knowledge/write")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @Transactional
    @WithMockCustomUser
    void given_tagsBiggerThan3_then_response400() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        postRequest.setTags(Arrays.asList(
            new TagInfo("A"),
            new TagInfo("B"),
            new TagInfo("C"),
            new TagInfo("D")));

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/knowledge/write")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @Transactional
    @WithMockCustomUser
    void given_tagLengthBiggerThan10_then_response400() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        postRequest.setTags(Arrays.asList(
            new TagInfo("A"),
            new TagInfo("B"),
            new TagInfo("01234567890")));

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/knowledge/write")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @Transactional
    @WithMockCustomUser
    void given_invalidContent_then_response400() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        postRequest.setContent("");

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/knowledge/write")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        );

        // then
        resultActions.andExpect(jsonPath("status").value(400));
    }

    @Test
    @SqlGroup({
        @Sql("/data/init_member.sql")
    })
    @Transactional
    @WithMockCustomUser
    void given_request_then_response201() throws Exception {
        // given
        PostRequest postRequest = postRequest();
        Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
        memberRepository.save(member);

        // when
        MockHttpServletResponse response = mockMvc.perform(
            MockMvcRequestBuilders.post("/board/knowledge/write")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(postRequest))
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }


    PostRequest postRequest() {
        PostRequest request = new PostRequest();
        request.setTitle("test_title");
        request.setTopic("Tech 뉴스");
        request.setTags(Collections.emptyList());
        request.setContent("test_content");

        return request;
    }
}
