package com.innim.okkycopy.global.auth.filter;

import com.innim.okkycopy.global.auth.AuthService;
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.exception.FailValidationJwtException;
import com.innim.okkycopy.global.error.exception.InvalidTokenValueException;
import com.innim.okkycopy.global.util.JwtUtil;
import com.innim.okkycopy.global.util.RequestResponseUtil;
import com.innim.okkycopy.global.util.property.JwtProperty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import org.springframework.core.log.LogMessage;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtRefreshFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final AntPathRequestMatcher requiresAuthenticationRequestMatcher = new AntPathRequestMatcher("/refresh",
        "POST");

    public JwtRefreshFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        if (requiresRefresh(request)) {
            String authorization = request.getHeader("Authorization");
            if (authorization == null) {
                filterChain.doFilter(request, response);
                return;
            }

            long memberId;

            try {
                String token = extractToken(authorization);
                memberId = validateToken(token);
            } catch(InvalidTokenValueException ex) {
                RequestResponseUtil.makeExceptionResponseForFilter(response,
                    ErrorCode._400_INVALID_TOKEN_VALUE);
                return;
            } catch(ExpiredJwtException ex) {
                RequestResponseUtil.makeExceptionResponseForFilter(response,
                    ErrorCode._403_TOKEN_EXPIRED);
                return;
            } catch(FailValidationJwtException ex) {
                RequestResponseUtil.makeExceptionResponseForFilter(response,
                    ErrorCode._401_TOKEN_AUTHENTICATION_FAIL);
                return;
            }

            String accessToken = JwtUtil.generateAccessToken(memberId, new Date());
            RequestResponseUtil.addCookieWithHttpOnly(response, "accessToken", accessToken);

            return;

        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresRefresh(HttpServletRequest request) {
        if (requiresAuthenticationRequestMatcher.matches(request)) {
            return true;
        }
        if (logger.isTraceEnabled()) {
            logger
                .trace(LogMessage.format("Did not match request to %s", requiresAuthenticationRequestMatcher));
        }
        return false;
    }

    private String extractToken(String value) throws InvalidTokenValueException {
        if (isBearer(value) == false) {
            throw new InvalidTokenValueException("prefix for token is incorrect");
        }

        String token = value.substring(JwtProperty.prefix.length());
        if (token.equals("")) {
            throw new InvalidTokenValueException("token value is blank");
        }

        return token;
    }

    private boolean isBearer(String value) {
        return StringUtils.startsWithIgnoreCase(value, JwtProperty.prefix);
    }

    private Long validateToken(String token)
        throws ExpiredJwtException, FailValidationJwtException {

        Claims claims;

        try {
            claims = JwtUtil.validateToken(token);
            if (claims.getSubject().equals("refresh")) {
                Long userId = Long.valueOf((Integer) claims.get("uid"));
                Date loginDate = authService.selectMemberLoginDate(userId);
                Date lat = new Date((Long) claims.get("lat"));
                if (lat.equals(loginDate)) {
                    return userId;
                }

                throw new FailValidationJwtException("this token is not valid");
            }
            throw new FailValidationJwtException("it's not refresh token");

        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (FailValidationJwtException ex) {
            throw ex;
        }
    }
}
