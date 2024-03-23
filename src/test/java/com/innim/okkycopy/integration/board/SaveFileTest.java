package com.innim.okkycopy.integration.board;

import com.google.gson.Gson;
import com.innim.okkycopy.domain.board.dto.request.write.WriteRequest;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
public class SaveFileTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    MemberRepository memberRepository;
    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        initSecurityContext();
    }

    void initSecurityContext() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Member testMember = memberRepository.findById("test_id").get();
        CustomUserDetails principal = new CustomUserDetails(testMember);

        Authentication auth =
                UsernamePasswordAuthenticationToken.authenticated(principal, principal.getPassword(), principal.getAuthorities());
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    /**
     * In the test(@Spring Boot Test) MultipartConfig is not adjusted.
     * we don't know the reason but this test method can make
     * unexpected charge for s3
     */
//    @Test
//    void given_exceeded2MBFile_then_responseErrorCode() throws Exception {
//        // given
//        String fileName = "exceed_2mb_img.jpg";
//        String contentType = "jpg";
//        String filePath = "src/test/resources/" + fileName;
//        FileInputStream fileInputStream = new FileInputStream(filePath);
//
//        MockMultipartFile testImage = new MockMultipartFile(
//                "file",
//                fileName,
//                contentType,
//                fileInputStream
//        );
//
//        System.out.println(testImage.getSize());
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                multipart("/board/file/upload")
//                        .file(testImage)
//        );
//
//        // then
//        resultActions.andExpect(jsonPath("code").value(400027));
//    }
}
