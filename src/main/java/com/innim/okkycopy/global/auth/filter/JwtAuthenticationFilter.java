package com.innim.okkycopy.global.auth.filter;

import com.innim.okkycopy.global.auth.CustomUserDetailsService;
import com.innim.okkycopy.global.error.exception.StatusCodeException;
import com.innim.okkycopy.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;


@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private CustomUserDetailsService customUserDetailsService;
    private RequestMatcher[] useSessionRequests;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        for (RequestMatcher matcher : useSessionRequests) {
            if (matcher.matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String token = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("accessToken")) {
                    token = c.getValue();
                }
            }
        }

        if (token == null) {
            filterChain.doFilter(request, response);
        } else {
            Claims claims = JwtUtil.validateToken(token);
            if (claims.getSubject().equals("access")) {
                Long userId = Long.valueOf((Integer) claims.get("uid"));
                Authentication authResult = authenticate(userId);
                onSuccessfulAuthentication(authResult, request, response, filterChain);
            } else {
                filterChain.doFilter(request, response);
            }

        }
    }


    private Authentication authenticate(Long userId) throws StatusCodeException {

        UserDetails userDetails = customUserDetailsService.loadUserByUserId(
            userId);
        return new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
    }

    private void onSuccessfulAuthentication(Authentication authResult, HttpServletRequest request,
        HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authResult);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }


}
