//package com.innim.okkycopy.integration.board.comment;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//import com.innim.okkycopy.domain.member.repository.MemberRepository;
//import com.innim.okkycopy.domain.member.entity.Member;
//import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
//import com.innim.okkycopy.global.auth.CustomUserDetails;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//@SpringBootTest
//public class GetCommentsTest {
//
//    @Autowired
//    WebApplicationContext context;
//    @Autowired
//    OkkyMemberRepository memberRepository;
//    MockMvc mockMvc;
//
//    @BeforeEach
//    void init() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(context)
//            .apply(springSecurity())
//            .build();
//    }
//
//    @Test
//    void given_noExistPost_then_responseErrorCode() throws Exception {
//        // given
//        long postId = 1000L;
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//            MockMvcRequestBuilders.get("/board/posts/" + postId + "/comments")
//        );
//
//        // then
//        resultActions.andExpect(jsonPath("code").value(400021));
//    }
//
//    @Test
//    void given_correctPostId_then_responseCommentsInfo() throws Exception {
//        // given
//        long postId = 1L;
//
//        // when
//        MockHttpServletResponse response = mockMvc.perform(
//            MockMvcRequestBuilders.get("/board/posts/" + postId + "/comments")
//        ).andReturn().getResponse();
//
//        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//    }
//
//    @Test
//    void given_requestWithoutToken_then_responseWithoutRequesterInfo() throws Exception {
//        // given
//        long postId = 1L;
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//            MockMvcRequestBuilders.get("/board/posts/" + postId + "/comments")
//        );
//        MockHttpServletResponse response = resultActions.andReturn().getResponse();
//
//        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        resultActions.andExpect(jsonPath("$.comments[0].requesterInfo").isEmpty());
//    }
//
//    @Test
//    void given_requestWithToken_then_responseWithRequesterInfo() throws Exception {
//        // given
//        long postId = 1L;
//        initSecurityContext();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//            MockMvcRequestBuilders.get("/board/posts/" + postId + "/comments")
//        );
//        MockHttpServletResponse response = resultActions.andReturn().getResponse();
//
//        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        resultActions.andExpect(jsonPath("$.comments[0].requesterInfo").isNotEmpty());
//
//        clearSecurityContext();
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
//    void clearSecurityContext() {
//        SecurityContextHolder.clearContext();
//    }
//}
