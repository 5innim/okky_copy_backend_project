package com.innim.okkycopy.global.auth.filter;

import com.innim.okkycopy.global.auth.CustomUserDetailsService;
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.exception.FailValidationJwtException;
import com.innim.okkycopy.global.error.exception.InvalidTokenValueException;
import com.innim.okkycopy.global.error.exception.UserIdNotFoundException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
        .getContextHolderStrategy();
    private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();
    private CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = "";
        try {
            token = extractToken(authorization);
        } catch (InvalidTokenValueException ex) {
            RequestResponseUtil.makeExceptionResponseForFilter(response,
                ErrorCode._400_INVALID_TOKEN_VALUE);
            return;
        }

        try {
            Claims claims = validateToken(token);
            if (claims.getSubject().equals("access")) {
                Long userId = Long.valueOf((Integer) claims.get("uid"));

                Authentication authResult = authenticate(userId);
                onSuccessfulAuthentication(authResult, request, response);
            }
        } catch (ExpiredJwtException ex) {
            RequestResponseUtil.makeExceptionResponseForFilter(response,
                ErrorCode._403_TOKEN_EXPIRED);
            return;
        } catch (FailValidationJwtException ex) {
            RequestResponseUtil.makeExceptionResponseForFilter(response,
                ErrorCode._401_TOKEN_AUTHENTICATION_FAIL);
            return;
        }

        filterChain.doFilter(request, response);
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

    private Claims validateToken(String token)
        throws ExpiredJwtException, FailValidationJwtException {

        Claims claims;

        try {
            claims = JwtUtil.validateToken(token);
            return claims;
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (FailValidationJwtException ex) {
            throw ex;
        }
    }

    private Authentication authenticate(Long userId) throws FailValidationJwtException {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUserId(userId);

            return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        } catch(UserIdNotFoundException ex) {
            throw new FailValidationJwtException(ex.getMessage());
        }

    }

    private void onSuccessfulAuthentication(Authentication authResult, HttpServletRequest request, HttpServletResponse response) {
        SecurityContext securityContext = securityContextHolderStrategy.createEmptyContext();
        securityContext.setAuthentication(authResult);
        securityContextRepository.saveContext(securityContext, request, response);
    }

}
