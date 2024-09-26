package com.innim.okkycopy.global.auth.filter;

import com.innim.okkycopy.global.auth.CustomOAuth2User;
import com.innim.okkycopy.global.auth.dto.response.OAuthInfoResponse;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.util.EncryptionUtil;
import com.innim.okkycopy.global.util.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class OAuth2SessionInfoProcessingFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher = new AntPathRequestMatcher("/oauth/info",
        "GET");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            try {
                String principalName = EncryptionUtil.base64Decode(request.getParameter("id"));
                CustomOAuth2User oAuth2User = (CustomOAuth2User) request.getSession().getAttribute(principalName);
                OAuthInfoResponse oAuthInfo = null;
                switch (oAuth2User.getRegistrationId()) {
                    case "google":
                        oAuthInfo = OAuthInfoResponse.builder()
                            .name(oAuth2User.getAttribute("name"))
                            .email(oAuth2User.getAttribute("email"))
                            .profile(oAuth2User.getAttribute("picture"))
                            .nickname(null)
                            .provider(oAuth2User.getRegistrationId())
                            .build();
                        break;
                    case "kakao":
                        LinkedHashMap<String, String> properties = oAuth2User.getAttribute("properties");
                        LinkedHashMap<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
                        if (properties == null || kakaoAccount == null) {
                            throw new NullPointerException();
                        }

                        LinkedHashMap<String, Boolean> kakaoAccountProfile =
                            (LinkedHashMap<String, Boolean>) kakaoAccount.get("profile");

                        oAuthInfo = OAuthInfoResponse.builder()
//                            .name(properties.get("name")) TODO "After authenticate business app at kakao, can use name property"
                            .email((String) kakaoAccount.get("email"))
                            .profile(
                                kakaoAccountProfile.get("is_default_image") ? null : properties.get("profile_image"))
                            .nickname(properties.get("nickname"))
                            .provider(oAuth2User.getRegistrationId())
                            .build();
                        break;
                    case "naver":
                        LinkedHashMap<String, String> map = oAuth2User.getAttribute("response");

                        if (map == null) {
                            throw new NullPointerException();
                        }

                        oAuthInfo = OAuthInfoResponse.builder()
                            .name(map.get("name"))
                            .email(map.get("email"))
                            .profile(map.get("profile_image"))
                            .nickname(map.get("nickname"))
                            .provider(oAuth2User.getRegistrationId())
                            .build();
                        break;
                }

                if (oAuthInfo == null) {
                    throw new NullPointerException();
                }

                ResponseUtil.setResponseToObject(response, oAuthInfo);

            } catch (Exception ex) {
                throw new StatusCode400Exception(ErrorCase._400_NO_ACCEPTABLE_PARAMETER);
            }


        } else {
            filterChain.doFilter(request, response);
        }
    }
}
