package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.service.GoogleMemberService;
import com.innim.okkycopy.domain.member.service.KakaoMemberService;
import com.innim.okkycopy.domain.member.service.NaverMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final GoogleMemberService googleMemberService;
    private final KakaoMemberService kakaoMemberService;
    private final NaverMemberService naverMemberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String nameAttributeKey = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
            .getUserNameAttributeName();

        String providerId = oAuth2User.getName();
        switch (registrationId) {
            case "google":
                return CustomOAuth2User.of(oAuth2User, nameAttributeKey, googleMemberService.findGoogleMember(providerId),
                    registrationId);
            case "kakao":
                return CustomOAuth2User.of(oAuth2User, nameAttributeKey, kakaoMemberService.findKakaoMember(providerId),
                    registrationId);
            case "naver":
                return CustomOAuth2User.of(oAuth2User, nameAttributeKey, naverMemberService.findNaverMember(providerId),
                    registrationId);
            default:
                return null;
        }
    }


}
