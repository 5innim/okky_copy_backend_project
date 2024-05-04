package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.entity.Member;
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
        Member member = null;
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String nameAttributeKey = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        String email = oAuth2User.getAttribute("email");
        switch(registrationId) {
            case "google":
                member = googleMemberService.findGoogleMember(email);
                break;
            case "kakao":
                member = kakaoMemberService.findKakaoMember(email);
                break;
            case "naver":
                member = naverMemberService.findNaverMember(email);
                break;
            default :
                break;
        }

        return CustomOAuth2User.of(oAuth2User, nameAttributeKey, member);
    }




}
