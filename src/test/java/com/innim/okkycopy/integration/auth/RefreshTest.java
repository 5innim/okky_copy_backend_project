package com.innim.okkycopy.integration.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.google.gson.Gson;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
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
public class RefreshTest {

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
    void given_emptyToken_then_response401001() throws Exception {
        // given
        String prefix = JwtProperty.prefix;
        String refreshToken = "";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/refresh")
            .header("Authorization", prefix + refreshToken)
        );

        // then
        resultActions.andExpect(jsonPath("code").value(401001));
    }

    @Test
    void given_expiredToken_then_response401003() throws Exception {
        // given
        String prefix = JwtProperty.prefix;
        String refreshToken = expiredToken();

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/refresh")
            .header("Authorization", prefix + refreshToken)
        );

        // then
        resultActions.andExpect(jsonPath("code").value(401003));
    }

    @Test
    void given_unCorrectSecretToken_then_response401001() throws Exception {
        // given
        String prefix = JwtProperty.prefix;
        String refreshToken = unCorrectSecretToken();

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/refresh")
            .header("Authorization", prefix + refreshToken)
        );

        // then
        resultActions.andExpect(jsonPath("code").value(401001));
    }

    @Test
    void given_unSavedMemberClaim_then_response401005() throws Exception {
        // given
        String prefix = JwtProperty.prefix;
        String refreshToken = notExistMemberToken();

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/refresh")
            .header("Authorization", prefix + refreshToken)
        );

        // then
        resultActions.andExpect(jsonPath("code").value(401005));
    }


    @Test
    @Transactional
    void given_unCorrectLoginDateToken_then_response401004() throws Exception {
        // given
        String prefix = JwtProperty.prefix;
        String refreshToken = correctToken();
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(new LoginContent("test_id", "test1234**"))));

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/refresh")
            .header("Authorization", prefix + refreshToken)
        );

        // then
        resultActions.andExpect(jsonPath("code").value(401004));
    }

    @Test
    @Transactional
    void given_correctRefreshToken_then_responseNewAccessToken() throws Exception {
        // given
        ResultActions loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(new LoginContent("test_id", "test1234**"))));

        String prefix = JwtProperty.prefix;
        String refreshToken = loginResult.andReturn().getResponse().getCookie("refreshToken").getValue();

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/refresh")
            .header("Authorization", prefix + refreshToken)
        );

        // then
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(JwtUtil.validateToken(response.getCookie("accessToken").getValue()).get("uid")).isEqualTo(1);
    }

    String expiredToken() {
        Date loginDate = new Date();
        Date expiredDate = new Date(loginDate.getTime() - JwtProperty.refreshValidTime);
        return JwtUtil.generateToken(1L, expiredDate, loginDate, "refresh");
    }

    String unCorrectSecretToken() {
        String generatedToken;
        Key unCorrectSecretKey = Keys.hmacShaKeyFor(
            Base64.getEncoder().encode("it's not a correct secret for generating secret key".getBytes()));
        Date loginDate = new Date();
        Date expiredDate = new Date(loginDate.getTime() + JwtProperty.refreshValidTime);
        try {
            generatedToken = Jwts.builder()
                .setHeader(Map.of("alg", JwtProperty.algorithm, "typ", "JWT"))
                .setExpiration(expiredDate)
                .setSubject("refresh")
                .addClaims(Map.of("uid", 1L))
                .addClaims(Map.of("lat", loginDate))
                .signWith(unCorrectSecretKey, JwtProperty.signatureAlgorithm)
                .compact();
        } catch (Exception ex) {
            throw new StatusCode500Exception(ErrorCase._500_JWT_GENERATE_FAIL);
        }

        return generatedToken;
    }

    String correctToken() {
        Date loginDate = new Date();
        Date expiredDate = new Date(loginDate.getTime() + JwtProperty.refreshValidTime);
        return JwtUtil.generateToken(1L, expiredDate, loginDate, "refresh");
    }

    String accessToken() {
        Date loginDate = new Date();
        Date expiredDate = new Date(loginDate.getTime() + JwtProperty.refreshValidTime);
        return JwtUtil.generateToken(1L, expiredDate, loginDate, "access");
    }

    String notExistMemberToken() {
        Date loginDate = new Date();
        Date expiredDate = new Date(loginDate.getTime() + JwtProperty.accessValidTime);
        return JwtUtil.generateToken(1111L, expiredDate, loginDate, "access");
    }

    private class LoginContent {

        String id;
        String password;

        public LoginContent(String id, String password) {
            this.id = id;
            this.password = password;
        }
    }
}
