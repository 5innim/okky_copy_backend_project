package com.innim.okkycopy.global.auth.filter;

import com.innim.okkycopy.domain.member.service.MemberLoginService;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCodeException;
import com.innim.okkycopy.global.util.JwtUtil;
import com.innim.okkycopy.global.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class RefreshJwtFilter extends OncePerRequestFilter {

    private MemberLoginService memberLoginService;

    private final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher("/refresh",
        "POST");

    public RefreshJwtFilter(MemberLoginService memberLoginService) {
        this.memberLoginService = memberLoginService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        if (requestMatcher.matches(request)) {
            String authorization = request.getHeader("Authorization");
            if (authorization == null || JwtUtil.prefixNotMatched(authorization)) {
                filterChain.doFilter(request, response);
            } else {
                String token = JwtUtil.extractTokenWithoutPrefix(authorization);
                Claims claims = JwtUtil.validateToken(token);
                if (claims.getSubject().equals("refresh")) {
                    Long userId = Long.valueOf((Integer) claims.get("uid"));
                    Date lat = new Date((Long) claims.get("lat"));

                    checkMostRecentGeneratedToken(userId, lat);

                    ResponseUtil.addCookieWithHttpOnly(response, "accessToken",
                        JwtUtil.generateAccessToken(userId, new Date()));
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }


    private void checkMostRecentGeneratedToken(Long userId, Date date) throws StatusCodeException {
        Date lastLoginDate = memberLoginService.findMemberLoginDate(userId);
        if (!date.equals(lastLoginDate)) {
            throw new StatusCode401Exception(ErrorCase._401_NOT_MOST_RECENT_GENERATED_TOKEN);
        }
    }
}
