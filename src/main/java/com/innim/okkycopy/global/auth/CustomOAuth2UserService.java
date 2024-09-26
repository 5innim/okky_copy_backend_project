package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.repository.GoogleMemberRepository;
import com.innim.okkycopy.domain.member.repository.KakaoMemberRepository;
import com.innim.okkycopy.domain.member.repository.NaverMemberRepository;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final GoogleMemberRepository googleMemberRepository;
    private final KakaoMemberRepository kakaoMemberRepository;
    private final NaverMemberRepository naverMemberRepository;

    @Transactional(readOnly = true)
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String nameAttributeKey = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
            .getUserNameAttributeName();

        switch (registrationId) {
            case "google":
                return CustomOAuth2User.of(oAuth2User, nameAttributeKey,
                    googleMemberRepository.findByProviderId(oAuth2User.getName()).orElse(null),
                    registrationId);
            case "kakao":
                return CustomOAuth2User.of(oAuth2User, nameAttributeKey,
                    kakaoMemberRepository.findByProviderId(oAuth2User.getName()).orElse(null),
                    registrationId);
            case "naver":
                try {
                    LinkedHashMap<String, String> map = oAuth2User.getAttribute("response");
                    if (map == null) {
                        throw new NullPointerException();
                    }
                    return CustomOAuth2User.of(oAuth2User, nameAttributeKey,
                        naverMemberRepository.findByProviderId(map.get("id")).orElse(null),
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
