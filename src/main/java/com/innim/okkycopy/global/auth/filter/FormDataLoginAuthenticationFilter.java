package com.innim.okkycopy.global.auth.filter;

import com.innim.okkycopy.domain.member.service.MemberCrudService;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.auth.dto.request.LoginRequest;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.util.JwtUtil;
import com.innim.okkycopy.global.util.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
public class FormDataLoginAuthenticationFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher = new AntPathRequestMatcher("/login",
        "POST");
    private AuthenticationManager authenticationManager;
    private MemberCrudService memberCrudService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (requestMatcher.matches(request)) {

            LoginRequest loginRequest = ResponseUtil.objectOf(request.getInputStream(), LoginRequest.class);
            String id = (loginRequest.getId() == null) ? "" : loginRequest.getId().strip();
            String password = (loginRequest.getPassword() == null) ? "" : loginRequest.getPassword().strip();

            Authentication authResult = attemptAuthentication(id, password);
            if (authResult == null) {
                throw new StatusCode401Exception(ErrorCase._401_LOGIN_FAIL);
            }
            successfulAuthentication(response, authResult);

        } else {
            filterChain.doFilter(request, response);
        }
    }

    private Authentication attemptAuthentication(String id, String password) {
        if (id.isEmpty() || password.isEmpty()) {
            throw new StatusCode400Exception(ErrorCase._400_BAD_FORM_DATA);
        }
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(id,
            password);
        try {
            return authenticationManager.authenticate(authRequest);
        } catch (AuthenticationException ex) {
            throw new StatusCode401Exception(ErrorCase._401_LOGIN_FAIL);
        }
    }

    private void successfulAuthentication(HttpServletResponse response, Authentication authResult) {
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        long userId = userDetails.getMember().getMemberId();

        String accessToken = "";
        String refreshToken = "";
        Date loginDate = new Date();
        accessToken = JwtUtil.generateAccessToken(userId, loginDate);
        refreshToken = JwtUtil.generateRefreshToken(userId, loginDate);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(loginDate.toInstant(), ZoneId.systemDefault());
        memberCrudService.modifyMemberLoginDate(userId, localDateTime);

        ResponseUtil.addCookie(response, "accessToken", accessToken);
        ResponseUtil.addCookie(response, "refreshToken", refreshToken);
    }

}
