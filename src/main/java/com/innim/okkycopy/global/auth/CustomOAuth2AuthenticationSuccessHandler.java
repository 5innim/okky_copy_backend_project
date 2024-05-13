package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.service.MemberCrudService;
import com.innim.okkycopy.global.util.JwtUtil;
import com.innim.okkycopy.global.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberCrudService memberCrudService;
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
            memberCrudService.modifyMemberLoginDate(userId, localDateTime);

            ResponseUtil.addCookieWithHttpOnly(response, "accessToken", JwtUtil.generateAccessToken(userId, loginDate));
            ResponseUtil.addCookieWithHttpOnly(response, "refreshToken",
                JwtUtil.generateRefreshToken(userId, loginDate));

            response.sendRedirect(frontendOrigin + basePath);
        } else {
            OAuth2AuthorizedClientId clientId = null;
            try {
                switch (customOAuth2User.getRegistrationId()) {
                    case "google":
                    case "kakao":
                        clientId = new OAuth2AuthorizedClientId(customOAuth2User.getRegistrationId(),
                            customOAuth2User.getName());
                        break;
                    case "naver":
                        LinkedHashMap<String, String> map = customOAuth2User.getAttribute("response");
                        assert map != null;

                        clientId = new OAuth2AuthorizedClientId(customOAuth2User.getRegistrationId(),
                            map.get("id"));
                        break;
                }
                assert clientId != null;
            } catch (Exception ex) {
                OAuth2Error oauth2Error = new OAuth2Error("provider_id_not_found");
                throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
            }

            request.getSession().setAttribute(String.valueOf(clientId.hashCode()), customOAuth2User);
            response.sendRedirect(frontendOrigin + signupPath + "?id=" + String.valueOf(clientId.hashCode()));

        }
    }
}
