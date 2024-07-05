//package com.innim.okkycopy.integration.board.community;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//import com.google.gson.Gson;
//import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
//import com.innim.okkycopy.domain.board.dto.request.write.TagInfo;
//import com.innim.okkycopy.domain.member.repository.MemberRepository;
//import com.innim.okkycopy.domain.member.entity.Member;
//import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
//import com.innim.okkycopy.global.auth.CustomUserDetails;
//import java.util.Arrays;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.context.WebApplicationContext;
//
//@SpringBootTest
//public class GetPostsTest {
//
//    @Autowired
//    WebApplicationContext context;
//    @Autowired
//    OkkyMemberRepository memberRepository;
//
//    MockMvc mockMvc;
//
//    @BeforeEach
//    void init() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(context)
//            .apply(springSecurity())
//            .build();
//
//        initSecurityContext();
//    }
//
//    void initSecurityContext() {
//        SecurityContext context = SecurityContextHolder.createEmptyContext();
//        Member testMember = memberRepository.findById("test_id").get();
//        CustomUserDetails principal = new CustomUserDetails(testMember);
//
//        Authentication auth =
//            UsernamePasswordAuthenticationToken.authenticated(principal, principal.getPassword(),
//                principal.getAuthorities());
//        context.setAuthentication(auth);
//        SecurityContextHolder.setContext(context);
//    }
//
//    @Test
//    @Transactional
//    void given_correctQueryStrings_then_returnPostsResponse() throws Exception {
//        // given
//        mockMvc.perform(
//            MockMvcRequestBuilders.post("/board/community/write")
//                .characterEncoding("UTF-8")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new Gson().toJson(writeRequest()))
//        );
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//            MockMvcRequestBuilders
//                .get("/board/community/posts?topicId=8&page=0&size=20&sort=likes,desc&sort=createdDate,desc")
//        );
//
//        // then
//        MockHttpServletResponse response = resultActions.andReturn().getResponse();
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        resultActions.andExpect(jsonPath("$.posts[0].title").value("test_title"));
//    }
//
//    PostRequest writeRequest() {
//        return PostRequest.builder()
//            .title("test_title")
//            .topic("사는얘기")
//            .tags(Arrays.asList(new TagInfo("tag1"), new TagInfo("tag2")))
//            .content("test_content")
//            .build();
//    }
//}