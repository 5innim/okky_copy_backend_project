package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.service.GoogleMemberService;
import com.innim.okkycopy.domain.member.service.KakaoMemberService;
import com.innim.okkycopy.domain.member.service.NaverMemberService;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
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

        switch (registrationId) {
            case "google":
                return CustomOAuth2User.of(oAuth2User, nameAttributeKey,
                    googleMemberService.findGoogleMember(oAuth2User.getName()),
                    registrationId);
            case "kakao":
                return CustomOAuth2User.of(oAuth2User, nameAttributeKey,
                    kakaoMemberService.findKakaoMember(oAuth2User.getName()),
                    registrationId);
            case "naver":
                try {
                    LinkedHashMap<String, String> map = oAuth2User.getAttribute("response");
                    assert map != null;
                    return CustomOAuth2User.of(oAuth2User, nameAttributeKey,
                        naverMemberService.findNaverMember(map.get("id")),
                        registrationId);
                } catch (Exception ex) {
                    OAuth2Error oauth2Error = new OAuth2Error("provider_id_not_found");
                    throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
                }

            default:
                return null;
        }
    }


}
