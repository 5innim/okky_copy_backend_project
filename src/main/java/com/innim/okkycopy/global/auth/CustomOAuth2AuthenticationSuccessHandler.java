package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.service.MemberLoginService;
import com.innim.okkycopy.global.util.JwtUtil;
import com.innim.okkycopy.global.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberLoginService memberLoginService;
    @Value("#{environment['frontend.origin']}")
    private String frontendOrigin;
    @Value("#{environment['frontend.path.signup']}")
    private String signupPath;
    @Value("#{environment['frontend.path.base']}")
    private String basePath;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        if (customOAuth2User.isSignedUpBefore()) {
            long userId = customOAuth2User.getMember().getMemberId();

            Date loginDate = new Date();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(loginDate.toInstant(), ZoneId.systemDefault());
            memberLoginService.modifyMemberLoginDate(userId, localDateTime);

            ResponseUtil.addCookieWithHttpOnly(response, "accessToken", JwtUtil.generateAccessToken(userId, loginDate));
            ResponseUtil.addCookieWithHttpOnly(response, "refreshToken", JwtUtil.generateRefreshToken(userId, loginDate));

            response.sendRedirect(frontendOrigin + basePath);
        } else {
            OAuth2AuthorizedClientId clientId = new OAuth2AuthorizedClientId(customOAuth2User.getRegistrationId(),
                customOAuth2User.getName());

            request.getSession().setAttribute(String.valueOf(clientId.hashCode()), customOAuth2User);
            response.sendRedirect(frontendOrigin + signupPath + "?id=" + String.valueOf(clientId.hashCode()));

        }
    }
}
