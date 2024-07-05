//package com.innim.okkycopy.integration.board.post;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//
//import com.google.gson.Gson;
//import com.innim.okkycopy.domain.member.entity.Member;
//import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
//import com.innim.okkycopy.global.auth.CustomUserDetails;
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
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.context.WebApplicationContext;
//
//@SpringBootTest
//public class ScrapTest {
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
//    void scrapPostTest() throws Exception {
//        // given
//        ScrapRequest scrapRequest = scrapRequest();
//
//        // when
//        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
//            .post("/board/post/scrap")
//            .contentType(MediaType.APPLICATION_JSON)
//            .characterEncoding("UTF-8")
//            .content(new Gson().toJson(scrapRequest))
//        ).andReturn().getResponse();
//
//        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//    }
//
//    @Test
//    @Transactional
//    void scrapDeleteTest() throws Exception {
//        // given
//        ScrapRequest scrapRequest = scrapRequest();
//        mockMvc.perform(MockMvcRequestBuilders
//            .post("/board/post/scrap")
//            .contentType(MediaType.APPLICATION_JSON)
//            .characterEncoding("UTF-8")
//            .content(new Gson().toJson(scrapRequest))
//        );
//
//        // when
//        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
//            .delete("/board/post/scrap")
//            .contentType(MediaType.APPLICATION_JSON)
//            .characterEncoding("UTF-8")
//            .content(new Gson().toJson(scrapRequest))
//        ).andReturn().getResponse();
//
//        // then
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//    }
//
//
//    ScrapRequest scrapRequest() {
//        return new ScrapRequest(1L);
//    }
//}
