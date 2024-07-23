package com.innim.okkycopy.global.auth.filter;

import com.innim.okkycopy.domain.member.service.MemberCrudService;
import com.innim.okkycopy.global.util.JwtUtil;
import com.innim.okkycopy.global.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class RefreshJwtFilter extends OncePerRequestFilter {

    private MemberCrudService memberCrudService;

    private final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher("/refresh",
        "POST");

    public RefreshJwtFilter(MemberCrudService memberCrudService) {
        this.memberCrudService = memberCrudService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        if (requestMatcher.matches(request)) {
            String token = null;
            if (request.getCookies() != null) {
                for (Cookie c : request.getCookies()) {
                    if (c.getName().equals("refreshToken")) {
                        token = c.getValue();
                    }
                }
            }

            if (token == null) {
                filterChain.doFilter(request, response);
            } else {
                Claims claims = JwtUtil.validateToken(token);
                if (claims.getSubject().equals("refresh")) {
                    Long userId = Long.valueOf((Integer) claims.get("uid"));
                    Date lat = new Date((Long) claims.get("lat"));

                    JwtUtil.checkTokenIsLogoutOrIsGeneratedBeforeLogin(memberCrudService.findMember(userId), lat);

                    ResponseUtil.addCookie(response, "accessToken",
                        JwtUtil.generateAccessToken(userId, new Date()));
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
