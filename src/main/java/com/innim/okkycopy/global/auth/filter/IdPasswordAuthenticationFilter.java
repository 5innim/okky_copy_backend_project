package com.innim.okkycopy.global.auth.filter;

import com.innim.okkycopy.global.auth.AuthService;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.auth.dto.request.LoginRequest;
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.exception.TokenGenerateException;
import com.innim.okkycopy.global.error.exception.UserIdNotFoundException;
import com.innim.okkycopy.global.util.JwtUtil;
import com.innim.okkycopy.global.util.RequestResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
public class IdPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login",
        "POST");
    private AuthService authService;
    private boolean postOnly = true;


    public IdPasswordAuthenticationFilter(AuthenticationManager authenticationManager, AuthService authService) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.authService = authService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response)
        throws AuthenticationException, IOException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        LoginRequest requestBody = obtainBody(request);
        if (requestBody.getId() == null) {
            requestBody.setId("");
        }
        if (requestBody.getPassword() == null) {
            requestBody.setPassword("");
        }

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(
            requestBody.getId(),
            requestBody.getPassword());

        return this.getAuthenticationManager().authenticate(authRequest);
    }


    @Nullable
    private LoginRequest obtainBody(HttpServletRequest request) throws IOException {
        return RequestResponseUtil.jsonToObject(request.getInputStream(), LoginRequest.class);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authResult) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        long userId = userDetails.getUserId();

        String accessToken = "";
        String refreshToken = "";
        try {
            Date loginDate = new Date();
            accessToken = JwtUtil.generateAccessToken(userId, loginDate);
            refreshToken = JwtUtil.generateRefreshToken(userId, loginDate);

            LocalDateTime localDateTime = LocalDateTime.ofInstant(loginDate.toInstant(), ZoneId.systemDefault());
            authService.modifyMemberLoginDate(userId, localDateTime);

        } catch (TokenGenerateException | UserIdNotFoundException ex) {
            RequestResponseUtil.makeExceptionResponseForFilter(response,
                ErrorCode._500_GENERATE_TOKEN);
            return;
        }

        RequestResponseUtil.addCookieWithHttpOnly(response, "accessToken", accessToken);
        RequestResponseUtil.addCookieWithHttpOnly(response, "refreshToken", refreshToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException failed) throws IOException {

        if (failed instanceof BadCredentialsException) {
            RequestResponseUtil.makeExceptionResponseForFilter(response, ErrorCode._400_BAD_CREDENTIALS);
            return;
        }

        RequestResponseUtil.makeExceptionResponseForFilter(response, ErrorCode._400_AUTHENTICATION_FAILED);

    }
}
