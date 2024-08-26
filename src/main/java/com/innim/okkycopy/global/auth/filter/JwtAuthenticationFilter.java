package com.innim.okkycopy.global.auth.filter;

import com.innim.okkycopy.domain.member.service.MemberCrudService;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.auth.CustomUserDetailsService;
import com.innim.okkycopy.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;


@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private CustomUserDetailsService customUserDetailsService;
    private RequestMatcher[] useSessionRequests;
    private MemberCrudService memberCrudService;


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
            log.info("Authentication start: " + request.getRequestURI());
            Claims claims = JwtUtil.validateToken(token);
            if (claims.getSubject().equals("access")) {
                Long userId = Long.valueOf((Integer) claims.get("uid"));
                Date lat = new Date((Long) claims.get("lat"));

                JwtUtil.checkTokenIsLogoutOrIsGeneratedBeforeLogin(memberCrudService.findMember(userId), lat);

                Authentication authResult = authenticate(userId);
                onSuccessfulAuthentication(authResult, request, response, filterChain);
            } else {
                log.info("Authentication fail");
                filterChain.doFilter(request, response);
            }

        }
    }


    private Authentication authenticate(Long userId) {

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

        log.info("Authentication success: " + ((CustomUserDetails) authResult.getPrincipal()).getUserId());

        filterChain.doFilter(request, response);
    }


}
