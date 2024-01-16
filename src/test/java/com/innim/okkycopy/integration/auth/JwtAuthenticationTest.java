package com.innim.okkycopy.integration.auth;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.*;

import com.google.gson.Gson;
import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.global.error.exception.TokenGenerateException;
import com.innim.okkycopy.global.util.JwtUtil;
import com.innim.okkycopy.global.util.property.JwtProperty;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class JwtAuthenticationTest {

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
    void given_notEqualPrefix_then_response400013() throws Exception {
        // given
        String prefix = "noBearer";
        String accessToken = "it's no Bearer prefix token";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/member/info")
            .header("Authorization", prefix + accessToken)
        );

        // then
        resultActions.andExpect(jsonPath("code", 400013).exists());
    }

    @Test
    void given_emptyToken_then_response400013() throws Exception {
        // given
        String prefix = JwtProperty.prefix;
        String accessToken = "";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/member/info")
            .header("Authorization", prefix + accessToken)
        );

        // then
        resultActions.andExpect(jsonPath("code", 400013).exists());
    }

    @Test
    void given_expiredToken_then_response403001() throws Exception {
        // given
        String prefix = JwtProperty.prefix;
        String accessToken = expiredToken();

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/member/info")
            .header("Authorization", prefix + accessToken)
        );

        // then
        resultActions.andExpect(jsonPath("code", 403001).exists());
    }

    @Test
    void given_unCorrectSecretToken_then_response401001() throws Exception {
        // given
        String prefix = JwtProperty.prefix;
        String accessToken = unCorrectSecretToken();

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/member/info")
            .header("Authorization", prefix + accessToken)
        );

        // then
        resultActions.andExpect(jsonPath("code", 401001).exists());
    }

    @Test
    void given_unSavedMemberClaim_then_response401001() throws Exception {
        // given
        String prefix = JwtProperty.prefix;
        String accessToken = correctToken();

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/member/info")
            .header("Authorization", prefix + accessToken)
        );

        // then
        resultActions.andExpect(jsonPath("code", 401001).exists());
    }

    @Test
    @Transactional
    void given_validToken_then_response200() throws Exception {
        // given
        String prefix = JwtProperty.prefix;
        String accessToken = correctToken();

        SignupRequest signupRequest = signupRequest();
        mockMvc.perform(MockMvcRequestBuilders.post("/member/signup")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(signupRequest)));

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/member/info")
            .header("Authorization", prefix + accessToken)
        );

        // then
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    String correctToken() {
        Date loginDate = new Date();
        Date expiredDate = new Date(loginDate.getTime() + JwtProperty.accessValidTime);
        return JwtUtil.generateToken(1l, expiredDate, loginDate, "access");
    }

    String expiredToken() {
        Date loginDate = new Date();
        Date expiredDate = new Date(loginDate.getTime() - JwtProperty.accessValidTime);
        return JwtUtil.generateToken(1l, expiredDate, loginDate, "access");
    }

    String unCorrectSecretToken() {
        String generatedToken;
        Key unCorrectSecretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode("it's not a correct secret for generating secret key".getBytes()));
        Date loginDate = new Date();
        Date expiredDate = new Date(loginDate.getTime() + JwtProperty.accessValidTime);
        try {
            generatedToken = Jwts.builder()
                .setHeader(Map.of("alg", JwtProperty.algorithm, "typ", "JWT"))
                .setExpiration(expiredDate)
                .setSubject("access")
                .addClaims(Map.of("uid", 1l))
                .addClaims(Map.of("lat", loginDate))
                .signWith(unCorrectSecretKey, JwtProperty.signatureAlgorithm)
                .compact();
        } catch(Exception ex) {
            throw new TokenGenerateException("can not generate token with userId: " + "[" + 1l + "]");
        }

        return generatedToken;
    }

    private SignupRequest signupRequest() {
        return SignupRequest.builder()
            .id("test1234")
            .password("test1234**")
            .email("test@test.com")
            .name("testName")
            .nickname("testNickname")
            .emailCheck(true)
            .build();
    }

}
